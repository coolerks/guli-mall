package top.integer.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.SneakyThrows;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import top.integer.common.utils.PageUtils;
import top.integer.common.utils.Query;
import top.integer.common.vo.ProductInfoVo;
import top.integer.gulimall.product.dao.SkuInfoDao;
import top.integer.gulimall.product.entity.SkuImagesEntity;
import top.integer.gulimall.product.entity.SkuInfoEntity;
import top.integer.gulimall.product.entity.SpuInfoDescEntity;
import top.integer.gulimall.product.entity.SpuInfoEntity;
import top.integer.gulimall.product.service.*;
import top.integer.gulimall.product.vo.SkuItemVo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {
    @Autowired
    private SkuImagesService skuImagesService;
    @Autowired
    private SpuInfoDescService spuInfoDescService;
    @Autowired
    private AttrGroupService attrGroupService;
    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

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

    @SneakyThrows
    @Override
    public SkuItemVo item(Long skuId) {
        SkuItemVo spuItemVo = new SkuItemVo();
        CompletableFuture<SkuInfoEntity> getSkuInfoCompletableFuture = CompletableFuture
                .supplyAsync(() -> this.getById(skuId), threadPoolExecutor);
        // 获取sku图片信息 skuImages表
        CompletableFuture<List<SkuImagesEntity>> getImages = CompletableFuture
                .supplyAsync(() -> skuImagesService.getImagesBySkuId(skuId))
                .whenComplete((r, e) -> spuItemVo.setImages(r));

        // 获取sku基本信息 skuInfo表
        SkuInfoEntity skuInfo = getSkuInfoCompletableFuture.get();
        spuItemVo.setInfo(skuInfo);

        // 获取spu销售属性组合 sku_sale_attr_value表
        CompletableFuture<List<SkuItemVo.ItemSaleAttrsVo>> getSaleAttr = CompletableFuture
                .supplyAsync(() -> skuSaleAttrValueService.getSaleAttrsBySpuId(skuInfo.getSpuId()))
                .whenComplete((r, e) -> spuItemVo.setSaleAttr(r));

        // 获取spu介绍信息 spuInfo表
        CompletableFuture<SpuInfoDescEntity> getDesp = CompletableFuture
                .supplyAsync(() -> spuInfoDescService.getDescBySpuId(skuInfo.getSpuId()))
                .whenComplete((r, e) -> spuItemVo.setDesp(r));
        // 获取spu属性 product_attr_value表
        CompletableFuture<List<SkuItemVo.SpuItemAttrGroupVo>> getGroupAttr = CompletableFuture
                .supplyAsync(() -> attrGroupService.getAttrgroupWithAttrsBySpuId(skuInfo.getSpuId(), skuInfo.getCatalogId()))
                .whenComplete((r, e) -> spuItemVo.setGroupAttrs(r));

        CompletableFuture.allOf(getImages, getSaleAttr, getDesp, getGroupAttr).get();
        return spuItemVo;
    }

    @Override
    public Map<Long, BigDecimal> getPrice(List<Long> ids) {
        return this.list(new LambdaQueryWrapper<SkuInfoEntity>()
                        .select(SkuInfoEntity::getSkuId, SkuInfoEntity::getPrice)
                        .in(SkuInfoEntity::getSkuId, ids)
                )
                .stream()
                .collect(Collectors.toMap(SkuInfoEntity::getSkuId, SkuInfoEntity::getPrice));
    }

    @Override
    public Map<Long, ProductInfoVo> getSpuInfoBySkuIds(List<Long> ids) {
        List<ProductInfoVo> result = baseMapper.getSpuInfoBySkuIds(ids.stream().distinct().toList());
        result.forEach(it -> {
            String spuPic = it.getSpuPic();
            String[] split = spuPic.split(",");
            it.setSpuPic(split[0]);
        });
        return result.stream()
                .collect(Collectors.toMap(ProductInfoVo::getSkuId, it -> it));
    }

}
