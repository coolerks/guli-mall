package top.integer.gulimall.ware.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class StockVo {
    private Long skuId;
    private List<Ware> wares;

    @Data
    @ToString
    public static class Ware {
        private Long id;
        private Integer stock;
    }
}
