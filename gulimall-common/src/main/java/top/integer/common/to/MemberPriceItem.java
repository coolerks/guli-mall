package top.integer.common.to;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MemberPriceItem {
    private BigDecimal price;
    private String name;
    private Long id;
}
