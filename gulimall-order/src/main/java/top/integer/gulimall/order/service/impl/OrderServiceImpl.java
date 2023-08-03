package top.integer.gulimall.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import io.seata.core.context.RootContext;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import top.integer.common.to.LockStockTo;
import top.integer.common.to.mq.OrderCloseTo;
import top.integer.common.utils.PageUtils;
import top.integer.common.utils.Query;
import top.integer.common.utils.R;
import top.integer.common.vo.ProductInfoVo;
import top.integer.common.vo.UserInfo;
import top.integer.common.vo.WareSkuLockVo;
import top.integer.gulimall.order.constant.OrderConstant;
import top.integer.gulimall.order.dao.OrderDao;
import top.integer.gulimall.order.entity.OrderEntity;
import top.integer.gulimall.order.entity.OrderItemEntity;
import top.integer.gulimall.order.enums.OrderStatusEnum;
import top.integer.gulimall.order.feign.CartFeign;
import top.integer.gulimall.order.feign.MemberAddressFeign;
import top.integer.gulimall.order.feign.SkuInfoFeign;
import top.integer.gulimall.order.feign.WmsFeign;
import top.integer.gulimall.order.interceptor.OrderInterceptor;
import top.integer.gulimall.order.service.OrderItemService;
import top.integer.gulimall.order.service.OrderService;
import top.integer.gulimall.order.vo.*;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {
    private static ThreadLocal<OrderSubmitVo> orderSubmitVoThreadLocal = new ThreadLocal<>();
    @Autowired
    private MemberAddressFeign addressFeign;

    @Autowired
    private CartFeign cartFeign;

    @Autowired
    private SkuInfoFeign skuInfoFeign;

    @Autowired
    private ThreadPoolExecutor executor;
    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private WmsFeign wmsFeign;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private StringRedisTemplate template;
    private final String SCRIPT = """
            if redis.call("get",KEYS[1]) == ARGV[1]
            then
                return redis.call("del",KEYS[1])
            else
                return 0
            end
            """;

    @Autowired
    private DataSource dataSource;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException {
        Long userId = OrderInterceptor.loginUser.get().getUserId();
        OrderConfirmVo orderConfirmVo = new OrderConfirmVo();

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();


        CompletableFuture<Void> getAddress = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            List<MemberAddressVo> memberAddress = addressFeign.getMemberAddress(userId);
            orderConfirmVo.setAddress(memberAddress);
            memberAddress.stream()
                    .filter(it -> it.getDefaultStatus() == 1)
                    .findFirst()
                    .ifPresent(orderConfirmVo::setDefaultAddress);
        }, executor);

        CompletableFuture<List<OrderItemVo>> getCartList = CompletableFuture.supplyAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            return cartFeign.getCurrentUserCartItems();
        }, executor);

        CompletableFuture<Void> getPrice = getCartList.thenAccept(orderItemVos -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            List<Long> skuIds = orderItemVos.stream().map(OrderItemVo::getSkuId).toList();
            Map<Long, BigDecimal> skusPrice = skuInfoFeign.getSkusPrice(skuIds);
            orderItemVos.forEach(it -> it.setPrice(skusPrice.get(it.getSkuId())));
            orderConfirmVo.setItems(orderItemVos);
        });

        CompletableFuture<Void> getStock = getCartList.thenAccept(orderItemVos -> {
            List<Long> skuIds = orderItemVos.stream().map(OrderItemVo::getSkuId).toList();
            R r = wmsFeign.hasStock(skuIds);
            Map<Long, Boolean> data = r.getData(new TypeReference<Map<Long, Boolean>>() {
            });
            orderItemVos.forEach(it -> it.setHasStock(data.getOrDefault(it.getSkuId(), false)));
        });

        String token = UUID.randomUUID().toString().replace("-", "");
        orderConfirmVo.setOrderToken(token);
        template.opsForValue().set(OrderConstant.USER_ORDER_TOKEN_PREFIX + userId, token, 30, TimeUnit.MINUTES);

        CompletableFuture.allOf(getAddress, getPrice, getStock).get();
        return orderConfirmVo;
    }

    @Override
//    @GlobalTransactional
    @Transactional
    public SubmitOrderResponseVo submitOrder(OrderSubmitVo vo) throws ExecutionException, InterruptedException {
        orderSubmitVoThreadLocal.set(vo);
        Long userId = OrderInterceptor.loginUser.get().getUserId();
        SubmitOrderResponseVo responseVo = new SubmitOrderResponseVo();
        // 验证令牌
        Long result = Optional.ofNullable(template.execute(new DefaultRedisScript<>(SCRIPT, Long.class),
                        List.of(OrderConstant.USER_ORDER_TOKEN_PREFIX + userId), vo.getOrderToken()))
                .orElse(0L);
        if (result == 0) {
            responseVo.setCode(1);
            return responseVo;
        }

        OrderCreateTo order = createOrder();
//        System.out.println("RootContext.getXID() = " + RootContext.getXID());
//        System.out.println("dataSource = " + dataSource);

//        System.out.println("order = " + order);

        // 验价失败
        if (!order.getOrder().getPayAmount().equals(vo.getPayPrice())) {
            responseVo.setCode(2);
            return responseVo;
        }
        // 锁定库存
        this.save(order.getOrder());
        orderItemService.saveBatch(order.getOrderItems());
        List<LockStockTo> lockStockList = order.getOrderItems().stream()
                .map(it -> new LockStockTo(it.getSkuId(), it.getSkuQuantity())).toList();
        R r = wmsFeign.orderLockStock(new WareSkuLockVo(lockStockList, order.getOrder().getOrderSn()));
        if (r.getCode() != 0) {
            responseVo.setCode(2);
            throw new RuntimeException("库存不足");
        }
        orderSubmitVoThreadLocal.remove();

        responseVo.setOrder(order.getOrder());
        OrderCloseTo orderCloseTo = new OrderCloseTo();
        orderCloseTo.setId(order.getOrder().getId());
        orderCloseTo.setSn(order.getOrder().getOrderSn());
        rabbitTemplate.convertAndSend("order-event-exchange", "order.create.queue", orderCloseTo);
        return responseVo;
    }

    @Override
    public OrderEntity getOrderInfo(String orderSn) {
        return this.getOne(new LambdaQueryWrapper<OrderEntity>().eq(OrderEntity::getOrderSn, orderSn));
    }


    private OrderCreateTo createOrder() throws ExecutionException, InterruptedException {
        UserInfo userInfo = OrderInterceptor.loginUser.get();
        OrderSubmitVo orderSubmitVo = orderSubmitVoThreadLocal.get();
        OrderCreateTo orderCreateTo = new OrderCreateTo();

        // 创建订单号
        String orderSn = IdWorker.getTimeId();
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderSn(orderSn);

        OrderConfirmVo orderConfirmVo = confirmOrder();

        // 收货地址
        MemberAddressVo fare = orderConfirmVo.getDefaultAddress();
        orderEntity.setMemberId(userInfo.getUserId());
        orderSubmitVo.setAddrId(fare.getId());
        orderEntity.setFreightAmount(new BigDecimal("0"));
        orderEntity.setReceiverProvince(fare.getProvince());
        orderEntity.setReceiverCity(fare.getCity());
        orderEntity.setReceiverRegion(fare.getRegion());
        orderEntity.setReceiverDetailAddress(fare.getDetailAddress());
        orderEntity.setReceiverPostCode(fare.getPostCode());
        orderEntity.setReceiverName(fare.getName());
        orderEntity.setReceiverPhone(fare.getPhone());
        List<Long> ids = orderConfirmVo.getItems().stream().map(OrderItemVo::getSkuId).distinct().toList();
        Map<Long, ProductInfoVo> spuMap = skuInfoFeign.getSpuInfoBySkuIds(ids);

        List<OrderItemEntity> orderItemEntities = orderConfirmVo.getItems()
                .stream()
                .map(it -> {
                    OrderItemEntity orderItemEntity = new OrderItemEntity();
                    // 订单号信息
                    orderItemEntity.setOrderSn(orderSn);
                    // spu信息
                    ProductInfoVo productInfoVo = spuMap.get(it.getSkuId());
                    orderItemEntity.setSpuId(productInfoVo.getSpuId());
                    orderItemEntity.setSpuName(productInfoVo.getSpuName());
                    orderItemEntity.setSpuBrand(productInfoVo.getSpuBrand());
                    orderItemEntity.setSpuPic(productInfoVo.getSpuPic());
                    orderItemEntity.setCategoryId(productInfoVo.getCatalogId());
                    // sku信息
                    orderItemEntity.setSkuId(it.getSkuId());
                    orderItemEntity.setSkuName(it.getTitle());
                    orderItemEntity.setSkuPic(it.getImage());
                    orderItemEntity.setSkuPrice(it.getPrice());
                    orderItemEntity.setSkuAttrsVals(String.join(";", it.getSkuAttr()));
                    orderItemEntity.setSkuQuantity(it.getCount());
                    // 优惠信息
                    // 积分信息
                    orderItemEntity.setGiftGrowth(it.getPrice().intValue());
                    orderItemEntity.setGiftIntegration(it.getPrice().intValue());
                    return orderItemEntity;
                })
                .toList();
        orderCreateTo.setOrderItems(orderItemEntities);

        // 验价
        orderEntity.setPromotionAmount(new BigDecimal("0"));
        orderEntity.setCouponAmount(new BigDecimal("0"));
        orderEntity.setIntegrationAmount(new BigDecimal("0"));
        BigDecimal sum = orderCreateTo.getOrderItems()
                .stream()
                .map(it -> it.getSkuPrice().multiply(BigDecimal.valueOf(it.getSkuQuantity())))
                .reduce(BigDecimal::add)
                .orElse(new BigDecimal("0"));

        orderEntity.setFreightAmount(new BigDecimal("0"));
        orderEntity.setIntegrationAmount(sum);
        orderEntity.setPayAmount(sum);
        orderEntity.setPromotionAmount(sum);
        orderCreateTo.setPayPrice(sum);


        // 设置订单状态
        orderEntity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        orderEntity.setAutoConfirmDay(7);

        // 设置总积分和成长值
        Integer growth = orderItemEntities.stream()
                .map(OrderItemEntity::getGiftGrowth)
                .reduce(Integer::sum)
                .orElse(0);
        Integer integration = orderItemEntities.stream()
                .map(OrderItemEntity::getGiftIntegration)
                .reduce(Integer::sum)
                .orElse(0);
        orderEntity.setGrowth(growth);
        orderEntity.setIntegration(integration);

        orderEntity.setCreateTime(new Date());
        orderEntity.setModifyTime(new Date());

        orderCreateTo.setOrder(orderEntity);

        return orderCreateTo;
    }

}
