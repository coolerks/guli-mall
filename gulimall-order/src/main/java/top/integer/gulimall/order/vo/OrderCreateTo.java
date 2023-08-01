package top.integer.gulimall.order.vo;

import lombok.Data;
import lombok.ToString;
import top.integer.gulimall.order.entity.OrderEntity;
import top.integer.gulimall.order.entity.OrderItemEntity;

import java.math.BigDecimal;
import java.util.List;

@Data
@ToString
public class OrderCreateTo {
    private OrderEntity order;
    private List<OrderItemEntity> orderItems;
    private BigDecimal payPrice;
    /**
     * 运费
     */
    private BigDecimal fare = new BigDecimal("0");
}
