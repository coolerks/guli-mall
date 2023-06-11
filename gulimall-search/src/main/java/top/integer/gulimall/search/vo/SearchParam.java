package top.integer.gulimall.search.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class SearchParam {
    private String keyword;
    private Long catalog3Id;
    /**
     * 指定排序字段，xxx_asc或者xxx_desc
     */
    private String sort;
    /**
     * 过滤条件
     * <ul>
     *     <li>hasStock - 是否有货</li>
     *     <li>
     *         <p>skuPrice - 价格区间，例如：</p>
     *         <p>100_200 - 100元到200元之间</p>
     *         <p>_200 - 最高200元</p>
     *         <p>200_ - 最低200元</p>
     *     </li>
     *     <li>brandId - 品牌id，可以有多个</li>
     *     <li>
     *         <p>attrs - 属性，例如：</p>
     *         <p>1_10寸:20寸&5_10米 - 代表1号属性两个值，二号属性一个值</p>
     *     </li>
     *     <li>pageNum - 页码</li>
     * </ul>
     */
    private Integer hasStock;
    private String skuPrice;
    private List<Long> brandId;
    private List<String> attrs;
    private Integer pageNum;
    private String queryString;
}
