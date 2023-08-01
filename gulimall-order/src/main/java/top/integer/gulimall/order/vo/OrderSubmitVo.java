package top.integer.gulimall.order.vo;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@ToString
public class OrderSubmitVo {
    private Long addrId;
    private Integer payType;
    private String orderToken;

    /**
     * 验价
     */
    private BigDecimal payPrice;

    /**
     * 备注
     */
    private String note;
}
