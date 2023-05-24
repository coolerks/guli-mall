package top.integer.common.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SkuReductionTo {
    private Long skuId;
    /**
     * 满减
     */
    private BigDecimal fullPrice, reducePrice;
    /**
     * 满fullCount件，打discount折
     */
    private int fullCount;
    private BigDecimal discount;


    private int countStatus;
    private int priceStatus;
    private List<MemberPriceItem> memberPrice;
}
