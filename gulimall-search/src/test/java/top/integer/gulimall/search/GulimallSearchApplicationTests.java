package top.integer.gulimall.search;

import co.elastic.clients.elasticsearch._types.aggregations.AggregateBuilders;
import org.elasticsearch.client.analytics.StringStatsAggregationBuilder;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.*;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.ParsedAvg;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.*;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.*;
import top.integer.gulimall.search.entry.Attrs;
import top.integer.gulimall.search.entry.Bank;
import top.integer.gulimall.search.entry.Blog;
import top.integer.gulimall.search.entry.Father;
import top.integer.gulimall.search.repository.BlogRepository;
import top.integer.gulimall.search.repository.FatherRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@SpringBootTest
class GulimallSearchApplicationTests {

    @Autowired
    ElasticsearchRestTemplate template;

    @Test
    void contextLoads() {
        Blog b1 = new Blog(1L, "标题1", "这是内容1");
        Blog b2 = new Blog(2L, "标题2", "这是内容2");
        Blog b3 = new Blog(3L, "标题3", "这是内容3");
//        repository.saveAll(Arrays.asList(b1, b2, b3));
    }

    @Test
    void loading() {
//        for (Blog blog : repository.findAll()) {
//            System.out.println("blog = " + blog);
//        }
    }

    @Test
    void insert() {
//        repository.save(new Blog(5L, "标题", "内容888888"));
    }

    @Test
    void get() {
//        System.out.println("repository.getBlogById(5L) = " + repository.getBlogById(5L));
    }

    @Test
    void update() {

        Document document = Document.create();
        document.put("content", "你好，世界，你好，世界");
        template.update(UpdateQuery.builder("1").withDocument(document).build(), IndexCoordinates.of("my-blog"));
    }

    @Test
    void query01() {
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchAllQuery())
                .withQuery(QueryBuilders.rangeQuery("age").gte(10).lte(20))
                .build();
        SearchHits<Bank> search = template.search(query, Bank.class);
        List<Bank> list = search.get().map(SearchHit::getContent)
                .toList();
        System.out.println(list);
    }

    @Test
    void group() {
        TermsAggregationBuilder age = new TermsAggregationBuilder("年龄分段");
        age.field("gender.keyword");
        System.out.println("age.field() = " + age.field());
        List<AbstractAggregationBuilder<?>> list = Arrays.asList(
                age
        );
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchAllQuery())
                .withAggregations(list)
                .build();

        SearchHits<Bank> search = template.search(query, Bank.class);
        System.out.println("search.hasAggregations() = " + search.hasAggregations());
        AggregationsContainer<?> aggregations = search.getAggregations();
        ElasticsearchAggregations elasticsearchAggregations = (ElasticsearchAggregations) aggregations;
//        List<Aggregation> list1 = elasticsearchAggregations.aggregations().asList();
//        for (Aggregation aggregation : list1) {
//            System.out.println("aggregation = " + aggregation);
//            System.out.println("aggregation.getType() = " + aggregation.getType());
//            System.out.println("aggregation.getName() = " + aggregation.getName());
//            System.out.println("aggregation.getMetadata() = " + aggregation.getMetadata());
//        }
        ParsedStringTerms parsedLongTerms = elasticsearchAggregations.aggregations().get("年龄分段");
        for (Terms.Bucket bucket : parsedLongTerms.getBuckets()) {
            System.out.println("bucket = " + bucket);
            Object key = bucket.getKey();
            long docCount = bucket.getDocCount();
            System.out.printf("{ key = %s, = docCount = %d}", key,  docCount);
        }
//        System.out.println("search.get().toList() = " + search.get().toList());
    }

    @Test
    void bankAgg() {
        TermsAggregationBuilder ageCount = new TermsAggregationBuilder("年龄");
        ageCount.field("age");

        TermsAggregationBuilder genderCount = new TermsAggregationBuilder("性别");
        genderCount.field("gender.keyword");

        AvgAggregationBuilder genderAvgBalance = new AvgAggregationBuilder("性别平均薪资");
        genderAvgBalance.field("balance");

        AvgAggregationBuilder ageAvgBalance = new AvgAggregationBuilder("年龄段平均薪资");
        ageAvgBalance.field("balance");

        genderCount.subAggregation(genderAvgBalance);
        ageCount.subAggregation(genderCount);
        ageCount.subAggregation(ageAvgBalance);


        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchAllQuery())
                .withAggregations(ageCount)
                .build();

        SearchHits<Bank> search = template.search(query, Bank.class);
        Aggregations aggregations = (Aggregations) search.getAggregations().aggregations();
        ParsedTerms ageTerms = aggregations.get("年龄");
        List<? extends Terms.Bucket> buckets = ageTerms.getBuckets();
        for (Terms.Bucket bucket : buckets) {
            Object key = bucket.getKey();
            long docCount = bucket.getDocCount();
            System.out.println("{");
            System.out.println("  年龄: " + key + ",");
            System.out.println("  数量: " + docCount + ",");
            ParsedAvg ageAvgBalanceParsed = bucket.getAggregations().get("年龄段平均薪资");
            System.out.println("  平均薪资: " + ageAvgBalanceParsed.getValue() + ",");
            System.out.println("  性别: [");
            ParsedTerms genderTerm = bucket.getAggregations().get("性别");
            for (Terms.Bucket gender : genderTerm.getBuckets()) {
                Object key1 = gender.getKey();
                long docCount1 = gender.getDocCount();
                System.out.println("  {");
                System.out.println("    性别: " + key1 + ",");
                System.out.println("    数量: " + docCount1);
                ParsedAvg avgBalance = gender.getAggregations().get("性别平均薪资");
                System.out.println("    平均工资: " + avgBalance.getValue());
                System.out.println("  },");
            }

            System.out.println("  ]");


//            System.out.println("  count: " + docCount + ",");
            System.out.println("}");
        }
    }

    @Test
    void nested() {
        Father father = new Father();
        father.setContent("这是内容...");
        father.setName("这是名字");
        father.setNum(new BigDecimal("11.22"));
        ArrayList<Attrs> list = new ArrayList<>();
        list.add(new Attrs(1L, "参数名1", "参数值1"));
        list.add(new Attrs(2L, "参数名2", "参数值2"));
        list.add(new Attrs(3L, "参数名3", "参数值3"));
        father.setList(list);
//        fatherRepository.save(father);
    }

    @Test
    void nestedAll() {
//        Iterator<Father> iterator = fatherRepository.findAll().iterator();
//        while (iterator.hasNext()) {
//            Father next = iterator.next();
//            System.out.println("next = " + next);
//        }
//        String[] split = "aaa:bbb:ccc:ddd:eee".split(":");
//        System.out.println("Arrays.toString(split) = " + Arrays.toString(split));
        String s = "1_10寸:20寸:30寸:40寸:50寸";
        String key = s.substring(0, s.indexOf("_"));
        String values = s.substring(s.indexOf("_") + 1);
        String[] value = values.split(":");
        System.out.println("key = " + key);
        System.out.println("values = " + values);
        System.out.println("Arrays.toString(value) = " + Arrays.toString(value));
    }
}
