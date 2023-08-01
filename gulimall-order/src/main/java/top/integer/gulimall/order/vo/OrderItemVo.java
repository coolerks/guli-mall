package top.integer.gulimall.order.vo;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Data
@ToString
public class OrderItemVo {
    private Long skuId;
    private Boolean check = true;
    private String title;
    private String image;
    private List<String> skuAttr;
    private BigDecimal price;
    private Integer count;
    private BigDecimal totalPrice;
    private Boolean hasStock;

    public BigDecimal getTotalPrice() {
        return price.multiply(new BigDecimal(count));
    }
}
