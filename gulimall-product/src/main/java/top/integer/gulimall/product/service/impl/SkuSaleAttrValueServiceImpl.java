package top.integer.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.integer.common.utils.PageUtils;
import top.integer.common.utils.Query;
import top.integer.gulimall.product.dao.SkuSaleAttrValueDao;
import top.integer.gulimall.product.entity.SkuSaleAttrValueEntity;
import top.integer.gulimall.product.service.SkuSaleAttrValueService;
import top.integer.gulimall.product.vo.SkuItemVo;

import java.util.List;
import java.util.Map;


@Service("skuSaleAttrValueService")
public class SkuSaleAttrValueServiceImpl extends ServiceImpl<SkuSaleAttrValueDao, SkuSaleAttrValueEntity> implements SkuSaleAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuSaleAttrValueEntity> page = this.page(
                new Query<SkuSaleAttrValueEntity>().getPage(params),
                new QueryWrapper<SkuSaleAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuItemVo.ItemSaleAttrsVo> getSaleAttrsBySpuId(Long spuId) {
        return baseMapper.getSaleAttrsBySpuId(spuId);
    }

}
