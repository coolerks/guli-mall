package top.integer.gulimall.order.vo;

import lombok.Data;
import lombok.ToString;
import top.integer.gulimall.order.entity.OrderEntity;

@Data
@ToString
public class SubmitOrderResponseVo {
    private OrderEntity order;
    /**
     * 状态码，0为成功
     */
    private Integer code = 0;
}
