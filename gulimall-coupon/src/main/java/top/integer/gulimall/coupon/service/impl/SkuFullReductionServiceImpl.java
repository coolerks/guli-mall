package top.integer.gulimall.coupon.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import top.integer.common.to.SkuReductionTo;
import top.integer.common.utils.PageUtils;
import top.integer.common.utils.Query;

import top.integer.gulimall.coupon.dao.SkuFullReductionDao;
import top.integer.gulimall.coupon.entity.MemberPriceEntity;
import top.integer.gulimall.coupon.entity.SkuFullReductionEntity;
import top.integer.gulimall.coupon.entity.SkuLadderEntity;
import top.integer.gulimall.coupon.service.MemberPriceService;
import top.integer.gulimall.coupon.service.SkuFullReductionService;
import top.integer.gulimall.coupon.service.SkuLadderService;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {
    @Autowired
    private SkuLadderService skuLadderService;

    @Autowired
    private MemberPriceService memberPriceService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuReduction(SkuReductionTo skuReductionTo) {
        // 满减 sms_sku_full_reduction
        if (skuReductionTo.getFullPrice().compareTo(new BigDecimal("0")) > 0) {
            SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
            BeanUtils.copyProperties(skuReductionTo, skuFullReductionEntity);
            skuFullReductionEntity.setAddOther(skuReductionTo.getPriceStatus());
            this.save(skuFullReductionEntity);
        }

        // 打折 sms_sku_ladder
        if (skuReductionTo.getFullCount() > 0) {
            SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
            BeanUtils.copyProperties(skuReductionTo, skuLadderEntity);
            skuLadderEntity.setAddOther(skuReductionTo.getPriceStatus());
            skuLadderService.save(skuLadderEntity);
        }

        // 会员价格 sms_member_price
        List<MemberPriceEntity> memberPriceEntities = skuReductionTo.getMemberPrice()
                .stream()
                .filter(it -> it.getPrice().compareTo(new BigDecimal("0")) > 0)
                .map(it -> {
                    MemberPriceEntity memberPrice = new MemberPriceEntity();
                    memberPrice.setSkuId(skuReductionTo.getSkuId());
                    memberPrice.setMemberLevelId(it.getId());
                    memberPrice.setMemberLevelName(it.getName());
                    memberPrice.setMemberPrice(it.getPrice());
                    memberPrice.setAddOther(1);
                    return memberPrice;
                }).toList();
        memberPriceService.saveBatch(memberPriceEntities);

    }

}
