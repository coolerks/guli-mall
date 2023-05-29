package top.integer.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import top.integer.common.to.SkuReductionTo;
import top.integer.common.to.SpuBoundTo;
import top.integer.common.utils.PageUtils;
import top.integer.common.utils.Query;

import top.integer.common.utils.R;
import top.integer.gulimall.product.dao.SpuInfoDao;
import top.integer.gulimall.product.entity.*;
import top.integer.gulimall.product.feign.CouponFeign;
import top.integer.gulimall.product.service.*;
import top.integer.gulimall.product.vo.spu.*;


@Service("spuInfoService")
@Transactional
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {
    @Autowired
    private SpuInfoDescService spuInfoDescService;
    @Autowired
    private SpuImagesService spuImagesService;
    @Autowired
    private AttrService attrService;
    @Autowired
    private ProductAttrValueService productAttrValueService;
    @Autowired
    private SkuInfoService skuInfoService;
    @Autowired
    private SkuImagesService skuImagesService;
    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;
    @Autowired
    private CouponFeign couponFeign;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String sidx = "null".equals(String.valueOf(params.get("sidx"))) ? null : String.valueOf(params.get("sidx"));
        String order = "null".equals(String.valueOf(params.get("order"))) ? null : String.valueOf(params.get("order"));
        String key = "null".equals(String.valueOf(params.get("key"))) ? null : String.valueOf(params.get("key"));
        String catelogId = "null".equals(String.valueOf(params.get("catelogId"))) ? null : String.valueOf(params.get("catelogId"));
        String brandId = "null".equals(String.valueOf(params.get("brandId"))) ? null : String.valueOf(params.get("brandId"));
        String status = "null".equals(String.valueOf(params.get("status"))) ? null : String.valueOf(params.get("status"));

        QueryWrapper<SpuInfoEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderBy(StringUtils.isNotBlank(sidx), "asc".equals(order), sidx);
        queryWrapper.eq(StringUtils.isNotBlank(catelogId), "catalog_id", catelogId);
        queryWrapper.eq(StringUtils.isNotBlank(brandId), "brand_id", brandId);
        queryWrapper.eq(StringUtils.isNotBlank(status), "publish_status", status);
        queryWrapper.and(StringUtils.isNotBlank(key), c -> c.eq("id", key)
                .or().eq("spu_name", "%" + key + "%")
                .or().eq("spu_description", "%" + key + "%")
        );
//                sidx: 'id',//排序字段
//                order: 'asc/desc',//排序方式
//                key: '华为',//检索关键字
//                catelogId: 6,//三级分类id
//                brandId: 1,//品牌id
//                status: 0,//商品状态

        IPage<SpuInfoEntity> page = this.page(new Query<SpuInfoEntity>().getPage(params), queryWrapper);
        return new PageUtils(page);
    }

    @Override
    public void saveInfo(SpuSaveVo spuInfo) {
        // 保存spu基本信息 pms_sku_info
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(spuInfo, spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        this.save(spuInfoEntity);

        // 保存描述信息 pms_spu_info_desc
        List<String> decript = spuInfo.getDecript();
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        spuInfoDescEntity.setSpuId(spuInfoEntity.getId());
        spuInfoDescEntity.setDecript(String.join(",", decript));
        spuInfoDescService.save(spuInfoDescEntity);

        // 保存图片集 pms_spu_images
        spuImagesService.saveImages(spuInfoEntity.getId(), spuInfo.getImages());

        // 保存属性集 pms_product_attr_value
        List<BaseAttrsItem> baseAttrs = spuInfo.getBaseAttrs();
        List<ProductAttrValueEntity> productAttrValues = baseAttrs.stream().map(it -> {
            ProductAttrValueEntity productAttrValue = new ProductAttrValueEntity();
            productAttrValue.setSpuId(spuInfoEntity.getId());
            productAttrValue.setAttrName(attrService.getById(it.getAttrId()).getAttrName());
            productAttrValue.setAttrId(it.getAttrId());
            productAttrValue.setQuickShow(it.getShowDesc());
            productAttrValue.setAttrValue(it.getAttrValues());
            return productAttrValue;
        }).toList();
        productAttrValueService.saveBatch(productAttrValues);

        // 保存spu中的所有sku

        List<SkusItem> skus = spuInfo.getSkus();
        for (SkusItem skusItem : skus) {
            // 保存基本信息 pms_sku_info
            SkuInfoEntity skuInfo = new SkuInfoEntity();
            BeanUtils.copyProperties(skusItem, skuInfo);
            skuInfo.setSpuId(spuInfoEntity.getId());
            skuInfo.setCatalogId(spuInfoEntity.getCatalogId());
            skuInfo.setBrandId(spuInfoEntity.getBrandId());
            skuInfo.setSkuDefaultImg(skusItem.getImages()
                    .stream()
                    .filter(it -> it.getDefaultImg() == 1)
                    .findFirst()
                    .orElseThrow()
                    .getImgUrl());
            skuInfo.setSaleCount(0L);
            skuInfoService.save(skuInfo);

            // 保存sku图片 pms_sku_images
            List<SkuImagesEntity> skuImagesEntities = skusItem.getImages().stream()
                    .filter(it -> StringUtils.isNotEmpty(it.getImgUrl()))
                    .map(it -> {
                        SkuImagesEntity skuImages = new SkuImagesEntity();
                        BeanUtils.copyProperties(it, skuImages);
                        skuImages.setSkuId(skuInfo.getSkuId());
                        return skuImages;
                    }).toList();
            skuImagesService.saveBatch(skuImagesEntities);

            // 保存销售属性 pms_sku_sale_attr_value
            List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = skusItem.getAttr().stream().map(it -> {
                SkuSaleAttrValueEntity skuSaleAttrValue = new SkuSaleAttrValueEntity();
                BeanUtils.copyProperties(it, skuSaleAttrValue);
                skuSaleAttrValue.setSkuId(skuInfo.getSkuId());
                return skuSaleAttrValue;
            }).toList();
            skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);

            // 保存sku优惠信息、满减信息 跨库，远程调用使用sms模块
            SkuReductionTo skuReductionTo = new SkuReductionTo();
            BeanUtils.copyProperties(skusItem, skuReductionTo);
            skuReductionTo.setSkuId(skuInfo.getSkuId());
            skuReductionTo.setMemberPrice(skusItem.getMemberPrice());


            if (skuReductionTo.getFullPrice().compareTo(new BigDecimal("0")) > 0 || skusItem.getFullCount() > 0) {
                R r = couponFeign.saveSkuReduction(skuReductionTo);
                if (r.getCode() != 0) {
                    log.error("远程保存优惠信息失败");
                }
            }


        }

        // 保存积分信息 sms_spu_bounds
        Bounds bounds = spuInfo.getBounds();
        SpuBoundTo spuBoundTo = new SpuBoundTo();
        BeanUtils.copyProperties(bounds, spuBoundTo);
        spuBoundTo.setSpuId(spuInfoEntity.getId());
        R r = couponFeign.saveSpuBounds(spuBoundTo);
        if (r.getCode() != 0) {
            log.error("远程保存积分信息失败");
        }
    }

}
