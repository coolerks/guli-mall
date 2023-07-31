package top.integer.gulimall.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import top.integer.common.utils.PageUtils;
import top.integer.common.utils.Query;
import top.integer.gulimall.order.dao.OrderDao;
import top.integer.gulimall.order.entity.OrderEntity;
import top.integer.gulimall.order.feign.CartFeign;
import top.integer.gulimall.order.feign.MemberAddressFeign;
import top.integer.gulimall.order.feign.SkuInfoFeign;
import top.integer.gulimall.order.interceptor.OrderInterceptor;
import top.integer.gulimall.order.service.OrderService;
import top.integer.gulimall.order.vo.MemberAddressVo;
import top.integer.gulimall.order.vo.OrderConfirmVo;
import top.integer.gulimall.order.vo.OrderItemVo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {
    @Autowired
    private MemberAddressFeign addressFeign;

    @Autowired
    private CartFeign cartFeign;

    @Autowired
    private SkuInfoFeign skuInfoFeign;

    @Autowired
    private ThreadPoolExecutor executor;

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
        }, executor);

        CompletableFuture<List<OrderItemVo>> getCartList = CompletableFuture
                .supplyAsync(() -> {
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

        CompletableFuture.allOf(getAddress, getPrice).get();
        return orderConfirmVo;
    }

}
