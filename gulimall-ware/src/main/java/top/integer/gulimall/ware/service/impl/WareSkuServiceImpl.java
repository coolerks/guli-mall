package top.integer.gulimall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.integer.common.to.LockStockTo;
import top.integer.common.to.mq.StockDetailTo;
import top.integer.common.to.mq.StockLockedTo;
import top.integer.common.utils.PageUtils;
import top.integer.common.utils.Query;
import top.integer.common.utils.R;
import top.integer.common.vo.WareSkuLockVo;
import top.integer.gulimall.ware.dao.WareSkuDao;
import top.integer.gulimall.ware.entity.WareOrderTaskDetailEntity;
import top.integer.gulimall.ware.entity.WareOrderTaskEntity;
import top.integer.gulimall.ware.entity.WareSkuEntity;
import top.integer.gulimall.ware.feign.ProductFeign;
import top.integer.gulimall.ware.service.WareOrderTaskDetailService;
import top.integer.gulimall.ware.service.WareOrderTaskService;
import top.integer.gulimall.ware.service.WareSkuService;
import top.integer.gulimall.ware.vo.StockVo;

import java.util.*;
import java.util.stream.Collectors;


@Service("wareSkuService")
@Slf4j
@EnableScheduling
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {
    @Autowired
    private ProductFeign productFeign;
    @Autowired
    private WareOrderTaskService wareOrderTaskService;
    @Autowired
    private WareOrderTaskDetailService wareOrderTaskDetailService;
    @Autowired
    private RabbitTemplate template;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String skuId = (String) params.get("skuId");
        String wareId = (String) params.get("wareId");
        LambdaQueryWrapper<WareSkuEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotBlank(skuId), WareSkuEntity::getSkuId, skuId)
                .eq(StringUtils.isNotBlank(wareId), WareSkuEntity::getWareId, wareId);
        IPage<WareSkuEntity> page = this.page(new Query<WareSkuEntity>().getPage(params), queryWrapper);

        return new PageUtils(page);
    }

    @Scheduled(cron = "0 * 1 * * *")
    public void clearStock() {
        log.info("定时清理库存....");
    }

    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        LambdaQueryWrapper<WareSkuEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WareSkuEntity::getWareId, wareId)
                .eq(WareSkuEntity::getSkuId, skuId);
        if (baseMapper.exists(queryWrapper)) {
            baseMapper.addStock(skuId, wareId, skuNum);
        } else {
            WareSkuEntity wareSku = new WareSkuEntity();
            try {
                R info = productFeign.info(skuId);
                if (Objects.equals(info.get("code"), 0)) {
                    Map<String, Object> skuInfo = (Map<String, Object>) info.get("skuInfo");
                    wareSku.setSkuName((String) skuInfo.get("skuName"));
                }
            } catch (Throwable e) {
                e.printStackTrace();
                log.error("远程调用获取sku的信息时出异常了...");
            }
            wareSku.setStock(skuNum);
            wareSku.setWareId(wareId);
            wareSku.setSkuId(skuId);
            baseMapper.insert(wareSku);
        }
    }

    @Override
    public Map<Long, Boolean> hasStock(List<Long> skuIds) {
        QueryWrapper<WareSkuEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("sku_id", " sum(stock) - sum(stock_locked) stock")
                .in("sku_id", skuIds)
                .groupBy("sku_id");
        List<WareSkuEntity> wareSkuEntities = baseMapper.selectList(queryWrapper);
        return wareSkuEntities.stream()
                .collect(Collectors.toMap(WareSkuEntity::getSkuId, w -> w.getStock() > 0));
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public boolean orderLockStock(WareSkuLockVo wareSkuLockVo) {
//        // 保存库存工作单，追溯都是从哪个仓库扣除的库存
        WareOrderTaskEntity orderTask = new WareOrderTaskEntity();
        orderTask.setOrderSn(wareSkuLockVo.getOrderSn());
        this.wareOrderTaskService.save(orderTask);

        List<LockStockTo> list = wareSkuLockVo.getList();
        List<StockVo> stockVos = baseMapper.listStock(list.stream().map(LockStockTo::getSkuId).toList());
        stockVos.forEach(it -> it.getWares().sort((a, b) -> b.getStock().compareTo(a.getStock())));
        Map<Long, Integer> stockMap = list.stream().collect(Collectors.toMap(LockStockTo::getSkuId, LockStockTo::getStock));
        boolean hasStock = stockVos.stream()
                .allMatch(stockVo -> stockVo.getWares().get(0).getStock() >= stockMap.get(stockVo.getSkuId())) &&
                list.size() == stockVos.size();
        List<WareOrderTaskDetailEntity> orderTaskDetails = new ArrayList<>();
        if (hasStock) {
            boolean all = true;
            for (StockVo stockVo : stockVos) {
                Long id = stockVo.getWares().get(0).getId();
                Integer stock = stockMap.get(stockVo.getSkuId());
                if (baseMapper.lockStock(id, stock) != 1) {
                    all = false;
                } else {
                    WareOrderTaskDetailEntity orderTaskDetail = new WareOrderTaskDetailEntity(null,
                            stockVo.getSkuId(), "", stock, orderTask.getId(), stockVo.getWares().get(0).getId(), 1);
                    orderTaskDetails.add(orderTaskDetail);
                }
                if (!all) {
                    throw new IllegalStateException("库存不足");
                }
            }
        } else {
            return false;
        }
        this.wareOrderTaskDetailService.saveBatch(orderTaskDetails);
        StockLockedTo stockLockedTo = new StockLockedTo();
        stockLockedTo.setId(orderTask.getId());
        template.convertAndSend("stock-event-exchange", "stock.lock", stockLockedTo);
        return true;
    }

}
