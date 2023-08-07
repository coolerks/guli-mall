package top.integer.gulimall.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.integer.common.utils.PageUtils;
import top.integer.common.utils.Query;
import top.integer.gulimall.coupon.dao.SeckillSessionDao;
import top.integer.gulimall.coupon.entity.SeckillSessionEntity;
import top.integer.gulimall.coupon.entity.SeckillSkuRelationEntity;
import top.integer.gulimall.coupon.service.SeckillSessionService;
import top.integer.gulimall.coupon.service.SeckillSkuRelationService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Service("seckillSessionService")
public class SeckillSessionServiceImpl extends ServiceImpl<SeckillSessionDao, SeckillSessionEntity> implements SeckillSessionService {
    @Autowired
    private SeckillSkuRelationService seckillSkuRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SeckillSessionEntity> page = this.page(
                new Query<SeckillSessionEntity>().getPage(params),
                new QueryWrapper<SeckillSessionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<SeckillSessionEntity> getLastest3DaysSku() {
        LocalDate now = LocalDate.now();
        Date start = Date.from(LocalDateTime.of(now, LocalTime.MIN).atZone(ZoneId.systemDefault()).toInstant());
        Date end = Date.from(LocalDateTime.of(now.plusDays(2), LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());

        LambdaQueryWrapper<SeckillSessionEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ge(SeckillSessionEntity::getStartTime, start)
                .le(SeckillSessionEntity::getEndTime, end);
        return this.list(queryWrapper)
                .stream()
                .peek(it -> it.setRelationSkus(
                        seckillSkuRelationService.list(new LambdaQueryWrapper<SeckillSkuRelationEntity>()
                                .eq(SeckillSkuRelationEntity::getPromotionSessionId, it.getId())
                        )
                )).toList();
    }

}
