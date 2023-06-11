package top.integer.gulimall.search.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import top.integer.common.utils.R;
import top.integer.gulimall.search.constant.EsConstant;
import top.integer.gulimall.search.entry.Product;
import top.integer.gulimall.search.feign.ProductFeign;
import top.integer.gulimall.search.service.MallSearchService;
import top.integer.gulimall.search.vo.AttrResponseVo;
import top.integer.gulimall.search.vo.SearchParam;
import top.integer.gulimall.search.vo.SearchResult;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MallSearchServiceImpl implements MallSearchService {
    @Autowired
    private ElasticsearchRestTemplate template;
    @Autowired
    private ProductFeign productFeign;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public SearchResult search(SearchParam searchParam) {
        // 构造bool查询
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        // 过滤数据
        // 拼接分类
        if (searchParam.getCatalog3Id() != null) {
            boolQuery.filter(QueryBuilders.termQuery("catalogId", searchParam.getCatalog3Id()));
        }
        // 拼接是否有销量
        if (searchParam.getHasStock() != null) {
            boolQuery.filter(QueryBuilders.termQuery("hasStock", searchParam.getHasStock() == 1));
        }
        // 拼接是否有品牌
        if (searchParam.getBrandId() != null && !searchParam.getBrandId().isEmpty()) {
            boolQuery.filter(QueryBuilders.termsQuery("brandId", searchParam.getBrandId()));
        }
        // 拼接属性
        if (searchParam.getAttrs() != null && !searchParam.getAttrs().isEmpty()) {
            searchParam.getAttrs().forEach(it -> {
                BoolQueryBuilder query = QueryBuilders.boolQuery();
                String key = it.substring(0, it.indexOf("_"));
                String values = it.substring(it.indexOf("_") + 1);
                String[] value = values.split(":");
                query.must(QueryBuilders.termQuery("attrs.attrId", Long.parseLong(key)));
                query.must(QueryBuilders.termsQuery("attrs.attrValue", value));
                // 第三个参数代表不参与评分
                boolQuery.filter(QueryBuilders.nestedQuery("attrs", query, ScoreMode.None));
            });
        }

        if (StringUtils.isNotBlank(searchParam.getSkuPrice())) {
            String skuPrice = searchParam.getSkuPrice();
            RangeQueryBuilder skuPriceRange = QueryBuilders.rangeQuery("skuPrice");

            if (skuPrice.startsWith("_")) {
                skuPriceRange.lte(new BigDecimal(skuPrice.substring(1)));
            } else if (skuPrice.endsWith("_")) {
                skuPriceRange.gte(new BigDecimal(skuPrice.substring(skuPrice.length() - 1)));
            } else {
                String[] split = skuPrice.split("_");
                skuPriceRange.gte(split[0]).lte(split[1]);
            }
            boolQuery.must(skuPriceRange);
        }

        int pageNum = searchParam.getPageNum() == null ? 0 : searchParam.getPageNum() - 1;
        PageRequest page = PageRequest.of(pageNum, EsConstant.PAGE_SIZE);

        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .withAggregations(aggregationBuilder())
                .withPageable(page);

        boolean hasKeyword = StringUtils.isNotBlank(searchParam.getKeyword());
        if (hasKeyword) {
            // 查询标题
            boolQuery.must(QueryBuilders.matchQuery("skuTitle", searchParam.getKeyword()));
            // 高亮
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("skuTitle")
                    .preTags("<b style='color:red'>")
                    .postTags("</b>");
            nativeSearchQueryBuilder.withHighlightBuilder(highlightBuilder);
        }
        // 排序
        if (StringUtils.isNotBlank(searchParam.getSort())) {
            String sort = searchParam.getSort();
            String[] split = sort.split("_");
            if (split.length == 2) {
                FieldSortBuilder order = SortBuilders.fieldSort(split[0])
                        .order("asc".equalsIgnoreCase(split[1]) ? SortOrder.ASC : SortOrder.DESC);
                nativeSearchQueryBuilder.withSorts(order);
            }
        }

        NativeSearchQuery query = nativeSearchQueryBuilder.build();
        SearchHits<Product> search = template.search(query, Product.class);

        SearchResult searchResult = new SearchResult();
        searchResult.setTotal(search.getTotalHits());
        List<Product> products = search.get().map(it -> {
            Product product = it.getContent();
            List<String> skuTitle = it.getHighlightField("skuTitle");
            if (hasKeyword && !skuTitle.isEmpty()) {
                product.setSkuTitle(skuTitle.get(0));
            }
            return product;
        }).toList();
        searchResult.setProducts(products);
        resolveAggregation((Aggregations) search.getAggregations().aggregations(), searchResult);
        searchResult.setPageNum(pageNum + 1);
        searchResult.setTotalPages((int) (search.getTotalHits() % EsConstant.PAGE_SIZE == 0 ?
                search.getTotalHits() / EsConstant.PAGE_SIZE : search.getTotalHits() / EsConstant.PAGE_SIZE + 1));
        System.out.println("searchResult = " + searchResult);


        // 6. 构建面包屑导航
        List<String> attrs = searchParam.getAttrs();
        if (attrs != null && attrs.size() > 0) {
            List<SearchResult.NavVo> navVos = attrs.stream().map(attr -> {
                String[] split = attr.split("_");
                SearchResult.NavVo navVo = new SearchResult.NavVo();
                //6.1 设置属性值
                navVo.setNavValue(split[1]);
                //6.2 查询并设置属性名
                try {
                    R r = productFeign.info(Long.parseLong(split[0]));
                    if (r.getCode() == 0) {
                        AttrResponseVo attrResponseVo = objectMapper.readValue(objectMapper.writeValueAsString(r.get("attr")), new TypeReference<AttrResponseVo>() {
                        });
                        navVo.setNavName(attrResponseVo.getAttrName());
                    }
                } catch (Exception e) {
                    log.error("远程调用商品服务查询属性失败", e);
                }
                //6.3 设置面包屑跳转链接
                String queryString = searchParam.getQueryString();
                String replace = queryString.replace("&attrs=" + attr, "").replace("attrs=" + attr+"&", "").replace("attrs=" + attr, "");
                navVo.setLink("http://search.gulimall.com/list.html" + (replace.isEmpty()?"":"?"+replace));
                return navVo;
            }).collect(Collectors.toList());
            searchResult.setNavs(navVos);
        }


        return searchResult;
    }

    /**
     * 解析分组聚合
     * @param aggregations 聚合
     * @param searchResult 设置的值
     */
    private void resolveAggregation(Aggregations aggregations, SearchResult searchResult) {
        ParsedLongTerms brand = aggregations.get("品牌");
        ParsedLongTerms category = aggregations.get("分类");
        ParsedNested attr = aggregations.get("属性");

        // 解析品牌
        List<SearchResult.BrandVo> brandVos = brand.getBuckets().stream().map(it -> {
            SearchResult.BrandVo brandVo = new SearchResult.BrandVo();
            Aggregations children = it.getAggregations();
            ParsedStringTerms brandName = children.get("品牌名称");
            ParsedStringTerms brandImg = children.get("品牌图片");

            brandVo.setBrandId(it.getKeyAsNumber().longValue());
            brandVo.setBrandName(brandName.getBuckets().get(0).getKeyAsString());
            brandVo.setBrandImg(brandImg.getBuckets().get(0).getKeyAsString());
            return brandVo;
        }).toList();

        // 解析分类
        List<SearchResult.CatalogVo> catalogVos = category.getBuckets().stream().map(it -> {
            SearchResult.CatalogVo catalogVo = new SearchResult.CatalogVo();
            ParsedStringTerms categoryName = it.getAggregations().get("分类名称");
            catalogVo.setCatalogId(it.getKeyAsNumber().longValue());
            catalogVo.setCatalogName(categoryName.getBuckets().get(0).getKeyAsString());
            return catalogVo;
        }).toList();

        // 解析属性
        List<SearchResult.AttrVo> attrVos = ((ParsedLongTerms) attr.getAggregations().get("属性id")).getBuckets().stream().map(it -> {
            SearchResult.AttrVo attrVo = new SearchResult.AttrVo();

            ParsedStringTerms attrName = it.getAggregations().get("属性名");
            ParsedStringTerms attrValues = it.getAggregations().get("属性值");
            List<String> attrValueList = attrValues.getBuckets().stream().map(MultiBucketsAggregation.Bucket::getKeyAsString).toList();
            attrVo.setAttrValue(attrValueList);

            attrVo.setAttrName(attrName.getBuckets().get(0).getKeyAsString());
            attrVo.setAttrId(it.getKeyAsNumber().longValue());
            return attrVo;
        }).toList();

        searchResult.setBrands(brandVos);
        searchResult.setCatalogs(catalogVos);
        searchResult.setAttrs(attrVos);
    }

    /**
     * 构造分组
     *
     * @return 分组集合
     */
    private List<AbstractAggregationBuilder<?>> aggregationBuilder() {
        TermsAggregationBuilder brand = new TermsAggregationBuilder("品牌");
        TermsAggregationBuilder category = new TermsAggregationBuilder("分类");
        NestedAggregationBuilder attrsGroup = new NestedAggregationBuilder("属性", "attrs");

        brand.field("brandId").size(10);
        TermsAggregationBuilder brandName = new TermsAggregationBuilder("品牌名称");
        TermsAggregationBuilder brandImg = new TermsAggregationBuilder("品牌图片");
        brandName.field("brandName").size(10);
        brandImg.field("brandImg").size(10);
        brand.subAggregation(brandName).subAggregation(brandImg);

        category.field("catalogId").size(10);
        TermsAggregationBuilder categoryName = new TermsAggregationBuilder("分类名称");
        category.subAggregation(categoryName);
        categoryName.field("catalogName").size(10);


        TermsAggregationBuilder attrId = new TermsAggregationBuilder("属性id");
        attrId.field("attrs.attrId").size(10);
        attrsGroup.subAggregation(attrId);

        TermsAggregationBuilder attrName = new TermsAggregationBuilder("属性名");
        attrName.field("attrs.attrName").size(10);
        attrId.subAggregation(attrName);

        TermsAggregationBuilder attrValue = new TermsAggregationBuilder("属性值");
        attrValue.field("attrs.attrValue").size(10);
        attrId.subAggregation(attrValue);


        return List.of(brand, category, attrsGroup);
    }
}
