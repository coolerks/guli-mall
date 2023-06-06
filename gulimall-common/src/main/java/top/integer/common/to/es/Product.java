package top.integer.common.to.es;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Data
@ToString
public class Product {
    private Long id;
    private Long skuId;
    private Long spuId;
    private String skuTitle;
    private BigDecimal skuPrice;
    private String skuImg;
    private Long saleCount;
    private Boolean hasStock;
    private Long hotScore;
    private Long brandId;
    private Long catalogId;
    private String brandName;
    private String brandImg;
    private String catalogName;
    private List<Attrs> attrs;
    public void setSkuId(Long skuId) {
        this.skuId = skuId;
        this.setId(skuId);
    }
}
