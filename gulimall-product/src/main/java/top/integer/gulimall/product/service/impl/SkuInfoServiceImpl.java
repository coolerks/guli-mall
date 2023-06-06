package top.integer.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import top.integer.common.utils.PageUtils;
import top.integer.common.utils.Query;

import top.integer.gulimall.product.dao.SkuInfoDao;
import top.integer.gulimall.product.entity.SkuInfoEntity;
import top.integer.gulimall.product.entity.SpuInfoEntity;
import top.integer.gulimall.product.service.SkuInfoService;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<SkuInfoEntity> queryWrapper = new QueryWrapper<>();

        String key = "null".equals(String.valueOf(params.get("key"))) ? null : String.valueOf(params.get("key"));
        String sidx = "null".equals(String.valueOf(params.get("sidx"))) ? null : String.valueOf(params.get("sidx"));
        String order = "null".equals(String.valueOf(params.get("order"))) ? null : String.valueOf(params.get("order"));
        String brandId = "null".equals(String.valueOf(params.get("brandId"))) ? null : String.valueOf(params.get("brandId"));
        String catelogId = "null".equals(String.valueOf(params.get("catelogId"))) ? null : String.valueOf(params.get("catelogId"));
        String min = "null".equals(String.valueOf(params.get("min"))) ? null : String.valueOf(params.get("min"));
        String max = "null".equals(String.valueOf(params.get("max"))) ? null : String.valueOf(params.get("max"));


        queryWrapper.orderBy(StringUtils.isNotBlank(sidx), "asc".equals(order), sidx);
        queryWrapper.eq(StringUtils.isNotBlank(catelogId) && !"0".equals(catelogId), "catalog_id", catelogId);
        queryWrapper.eq(StringUtils.isNotBlank(brandId) && !"0".equals(brandId), "brand_id", brandId);
        queryWrapper.ge(StringUtils.isNotBlank(min) && !new BigDecimal("0").equals(new BigDecimal(min)),
                        "price", new BigDecimal(min))
                        .le(StringUtils.isNotBlank(max) && !new BigDecimal("0").equals(new BigDecimal(max)),
                                "price", new BigDecimal(max));
        queryWrapper.and(StringUtils.isNotBlank(key), c -> c.eq("sku_id", key)
                .or().like("sku_name", "%" + key + "%")
        );

//                page: 1,//当前页码
//                limit: 10,//每页记录数
//                sidx: 'id',//排序字段
//                order: 'asc/desc',//排序方式
//                key: '华为',//检索关键字
//                catelogId: 0,
//                brandId: 0,
//                min: 0,
//                max: 0
        IPage<SkuInfoEntity> page = this.page(new Query<SkuInfoEntity>().getPage(params), queryWrapper);
        return new PageUtils(page);
    }

    @Override
    public List<SkuInfoEntity> getBySpuId(Long spuId) {
        LambdaQueryWrapper<SkuInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SkuInfoEntity::getSpuId, spuId);
        return this.list(queryWrapper);
    }

}
