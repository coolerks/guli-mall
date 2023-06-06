package top.integer.gulimall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CataLog1Vo {
    /**
     * 父分类id
     */
    private String catalog1Id;
    /**
     * 当前分类id
     */
    private String id;
    private String name;
    private List<CataLog3Vo> catalog3List;

    @Data
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CataLog3Vo {
        /**
         * 父分类id
         */
        private String catalog2Id;
        private String id, name;
    }
}
