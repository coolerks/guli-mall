package top.integer.common.to;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@ToString
public class SpuBoundTo {
    private Long spuId;
    private BigDecimal growBounds;
    private BigDecimal buyBounds;
}
