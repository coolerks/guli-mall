package top.integer.common.vo;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ProductInfoVo {
    private Long skuId;
    private Long spuId;
    private String spuName;
    private Long catalogId;
    private String spuBrand;
    private String spuPic;
}
