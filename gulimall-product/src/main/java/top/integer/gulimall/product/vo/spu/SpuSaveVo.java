package top.integer.gulimall.product.vo.spu;

import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class SpuSaveVo{
    private String spuDescription;
    private String spuName;
    private List<String> images;
    private Long catalogId;
    private List<SkusItem> skus;
    private List<BaseAttrsItem> baseAttrs;
    private Long brandId;
    private Bounds bounds;
    private BigDecimal weight;
    private List<String> decript;
    private int publishStatus;
}
