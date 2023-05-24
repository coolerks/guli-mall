package top.integer.gulimall.product.vo.spu;

import java.math.BigDecimal;
import java.util.List;
import lombok.Data;
import top.integer.common.to.MemberPriceItem;


@Data
public class SkusItem{
    private String skuTitle;
    private List<ImagesItem> images;
    private List<MemberPriceItem> memberPrice;
    private BigDecimal discount;
    private String skuSubtitle;
    private List<String> descar;
    private int priceStatus;
    private BigDecimal fullPrice;
    private String skuName;
    private BigDecimal price;
    private BigDecimal reducePrice;
    private int countStatus;
    private List<AttrItem> attr;
    private int fullCount;
}
