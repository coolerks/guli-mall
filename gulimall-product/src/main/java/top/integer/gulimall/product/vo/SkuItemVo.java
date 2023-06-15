package top.integer.gulimall.product.vo;

import lombok.Data;
import lombok.ToString;
import top.integer.gulimall.product.entity.SkuImagesEntity;
import top.integer.gulimall.product.entity.SkuInfoEntity;
import top.integer.gulimall.product.entity.SpuInfoDescEntity;

import java.util.List;

@ToString
@Data
public class SkuItemVo {
    /**
     * sku 描述信息
     */
    private SkuInfoEntity info;
    /**
     * sku 图片信息
     */
    private List<SkuImagesEntity> images;
    /**
     * spu 描述信息
     */
    private SpuInfoDescEntity desp;
    /**
     * spu 销售属性组合
     */
    private List<ItemSaleAttrsVo> saleAttr;
    /**
     * 规则参数信息
     */
    private List<SpuItemAttrGroupVo> groupAttrs;
    private Boolean hasStock = true;

    @Data
    @ToString
    public static class ItemSaleAttrsVo {
        private Long attrId;
        private String attrName;
        private List<AttrValueWithSkuIdVo> attrValues;
    }

    @Data
    @ToString
    public static class AttrValueWithSkuIdVo {
        private String attrValue;
        private String skuIds;
    }

    @Data
    @ToString
    public static class SpuItemAttrGroupVo {
        private String groupName;
        private List<SpuBaseAttrVo> attrs;
    }

    @Data
    @ToString
    public static class SpuBaseAttrVo {
        private String attrName;
        private String attrValue;
    }
}
