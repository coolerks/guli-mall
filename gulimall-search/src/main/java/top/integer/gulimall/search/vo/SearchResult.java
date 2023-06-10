package top.integer.gulimall.search.vo;

import lombok.Data;
import lombok.ToString;
import top.integer.gulimall.search.entry.Product;

import java.util.List;

/**
 * 搜索结果
 * <ul>
 *     <li>品牌</li>
 *     <li>分类</li>
 *     <li>属性</li>
 *     <li>具体的商品</li>
 *     <li>页码</li>
 *     <li>总数</li>
 *     <li>总页数</li>
 * </ul>
 */
@Data
public class SearchResult {
    private List<BrandVo> brands;
    private List<CatalogVo> catalogs;
    private List<AttrVo> attrs;
    private List<Product> products;
    private Integer pageNum;
    private Long total;
    private Integer totalPages;

    @Data
    @ToString
    public static class BrandVo {
        private Long brandId;
        private String brandName;
        private String brandImg;
    }

    @Data
    @ToString
    public static class AttrVo {
        private Long attrId;
        private String attrName;
        private List<String> attrValue;
    }

    @Data
    @ToString
    public static class CatalogVo {
        private Long catalogId;
        private String catalogName;
    }
}

