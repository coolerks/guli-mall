package top.integer.gulimall.search.entry;

import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;
import java.util.List;

@Document(indexName = "product")
@Data
@ToString
public class Product {
    @Id
    private Long id;
    @Field(type = FieldType.Long)
    private Long skuId;
    @Field(type = FieldType.Long)
    private Long spuId;
    @Field(type = FieldType.Text, analyzer = "ik_smart")
    private String skuTitle;
    @Field(type = FieldType.Keyword)
    private BigDecimal skuPrice;
    @Field(type = FieldType.Keyword, index = false, docValues = false)
    private String skuImg;
    @Field(type = FieldType.Long)
    private Long saleCount;
    @Field(type = FieldType.Boolean)
    private Boolean hasStock;
    @Field(type = FieldType.Long)
    private Long hotScore;
    @Field(type = FieldType.Long)
    private Long brandId;
    @Field(type = FieldType.Long)
    private Long catalogId;
    @Field(type = FieldType.Keyword, index = false, docValues = false)
    private String brandName;
    @Field(type = FieldType.Keyword, index = false, docValues = false)
    private String brandImg;
    @Field(type = FieldType.Keyword, index = false, docValues = false)
    private String catalogName;
    @Field(type = FieldType.Nested)
    private List<Attrs> attrs;


    public void setSkuId(Long skuId) {
        this.skuId = skuId;
        this.setId(skuId);
    }
}
