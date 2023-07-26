package top.integer.gulimall.cart.vo;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Data
@ToString
public class CartVo {
    private List<CartItemVo> items;
    /**
     * 商品数量
     */
    private Integer countNum;
    /**
     * 数量类型
     */
    private Integer countType;
    /**
     * 商品总价
     */
    private BigDecimal totalAmount;
    /**
     * 减免价格
     */
    private BigDecimal reduce;

    public Integer getCountNum() {
        return items == null ? 0 : items.stream().map(CartItemVo::getCount).reduce(Integer::sum).orElse(0);
    }

    public Integer getCountType() {
        return items == null ? 0 : items.size();
    }

    public BigDecimal getTotalAmount() {
        return items == null ? new BigDecimal(0) : items.stream()
                .filter(CartItemVo::getCheck)
                .map(CartItemVo::getTotalPrice)
                .reduce(BigDecimal::add)
                .orElse(new BigDecimal(0));

    }
}
