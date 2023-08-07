package top.integer.gulimall.seckill.schedule;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import top.integer.common.utils.R;
import top.integer.gulimall.seckill.feign.ProductFeign;
import top.integer.gulimall.seckill.feign.SeckillFeign;
import top.integer.gulimall.seckill.vo.SeckillSessionVo;
import top.integer.gulimall.seckill.vo.SeckillSkuRelationVo;
import top.integer.gulimall.seckill.vo.SeckillSkuVo;
import top.integer.gulimall.seckill.vo.SkuInfoVo;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class SeckillSchedule {
    @Autowired
    private SeckillFeign feign;
    @Autowired
    private ProductFeign productFeign;
    @Autowired
    private RedisTemplate<Object, Object> template;
    @Autowired
    private RedissonClient redissonClient;

    private static String SESSION_PREFIX = "seckill:sessions:";
    private static String SKUKILL_PREFIX = "seckill:skus";
    private static String SKU_STOCK_PREFIX = "seckill:stock:";
    private static String UPLOAD_LOCK = "seckill:upload:lock";

    public void uploadSeckillSku() {
        RLock lock = redissonClient.getLock(UPLOAD_LOCK);
        if (lock.tryLock()) {
            try {
                List<SeckillSessionVo> list = feign.getLastest3DaysSku();
                saveSessionInfo(list);
                saveSkuInfos(list);
            } finally {
                lock.unlock();
            }
        }
    }

    public void saveSessionInfo(List<SeckillSessionVo> list) {
        list.forEach(item -> {
            long start = item.getStartTime().getTime();
            long end = item.getEndTime().getTime();
            String key = SESSION_PREFIX + start + "_" + end;
            List<String> ids = item.getRelationSkus().stream().map(it -> it.getPromotionSessionId() + "_" + it.getSkuId()).toList();
            if (!ids.isEmpty() && Boolean.FALSE.equals(template.hasKey(key))) {
                template.opsForList().leftPushAll(key, ids);
            }
            System.out.println("ids = " + ids);
        });
    }

    public void saveSkuInfos(List<SeckillSessionVo> list) {
        BoundHashOperations<Object, Object, Object> ops = template.boundHashOps(SKUKILL_PREFIX);
        list.forEach(item -> {
            item.getRelationSkus().forEach(it -> {
                String skuKey = it.getPromotionSessionId() + "_" +it.getSkuId().toString();
                if (Boolean.TRUE.equals(ops.hasKey(skuKey))) {
                    return;
                }
                SeckillSkuVo seckillSkuVo = new SeckillSkuVo();
                BeanUtils.copyProperties(it, seckillSkuVo);
                // 查询sku信息
                R r = productFeign.info(it.getSkuId());
                SkuInfoVo skuInfo = r.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                });
                System.out.println("r = " + r);
                System.out.println("skuInfo = " + skuInfo);
                seckillSkuVo.setSkuInfo(skuInfo);
                seckillSkuVo.setStart(item.getStartTime().getTime());
                seckillSkuVo.setEnd(item.getEndTime().getTime());
                String uuid = UUID.randomUUID().toString().replace("-", "");
                seckillSkuVo.setRandomCode(uuid);

                if (Boolean.FALSE.equals(template.hasKey(SKU_STOCK_PREFIX + uuid))) {
                    RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_PREFIX + uuid);
                    semaphore.trySetPermits(it.getSeckillCount());
                }

                ops.put(skuKey, seckillSkuVo);
                System.out.println("seckillSkuVo = " + seckillSkuVo);
            });
        });
    }
}
