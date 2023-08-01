package top.integer.gulimall.order.vo;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Data
@ToString
public class OrderConfirmVo {
    /**
     * 收货地址
     */
    private List<MemberAddressVo> address;

    /**
     * 默认的收货地址
     */
    private MemberAddressVo defaultAddress;

    /**
     * 选中的购物项
     */
    private List<OrderItemVo> items;

    /**
     * 优惠券信息
     */
    private Integer integration;

    /**
     * 订单总价
     */
    private BigDecimal total;

    /**
     * 支付价格
     */
    private BigDecimal payPrice;

    /**
     * 防重令牌
     */
    private String orderToken;

    /**
     * 总的商品个数
     */
    private Integer count;


    public BigDecimal getTotal() {
        if (items == null || items.isEmpty()) {
            return new BigDecimal("0");
        }
        return items.stream()
                .map(it -> it.getPrice().multiply(new BigDecimal(it.getCount())))
                .reduce(BigDecimal::add).orElse(new BigDecimal("0"));
    }

    public Integer getCount() {
        if (items == null || items.isEmpty()) {
            return 0;
        }
        return items.stream()
                .map(OrderItemVo::getCount)
                .reduce(Integer::sum)
                .orElse(0);
    }

    public BigDecimal getPayPrice() {
        return getTotal();
    }
}
