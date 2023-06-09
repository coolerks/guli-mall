package top.integer.gulimall.product;

import com.aliyun.oss.OSS;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.StringRedisTemplate;
import top.integer.common.utils.R;
import top.integer.gulimall.product.feign.WareFeign;
import top.integer.gulimall.product.service.BrandService;
import top.integer.gulimall.product.service.CategoryService;
import top.integer.gulimall.product.vo.CataLog1Vo;

import java.util.List;
import java.util.Map;

@SpringBootTest
class GulimallProductApplicationTests {

    @Autowired
    BrandService brandService;

    @Autowired
    private OSS ossClient;

    @Autowired
    private WareFeign wareFeign;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void contextLoads() {
//		System.out.println("brandService = " + brandService);
//		BrandEntity brand = new BrandEntity();
//		brand.setName("华为");
//		brandService.save(brand);
//		System.out.println("brandService.list() = " + brandService.list());
        System.out.println("ossClient = " + ossClient);

    }

    @Test
    void hasStock() {
        R r = wareFeign.hasStock(List.of(6L, 45L));
        System.out.println("r = " + r);
        Map<Long, Boolean> data = r.getData(new TypeReference<Map<Long, Boolean>>() {
        });
        System.out.println("data = " + data);
        System.out.println("data.get(6L) = " + data.get(6L));
    }

    @Autowired
    private ThymeleafProperties thymeleafProperties;


    @Test
    void properties() {
        System.out.println("thymeleafProperties = " + thymeleafProperties);
    }

    @Test
    void testCategoryJson() throws JsonProcessingException {
        Map<String, List<CataLog1Vo>> catelogJson = categoryService.getCatelogJson();
        String json = objectMapper.writeValueAsString(catelogJson);
        System.out.println("json = " + json);
    }

    @Test
    void redisTest() {
        redisTemplate.opsForValue().set("aaa", "bbb");
        String s = redisTemplate.opsForValue().get("aaa");
        System.out.println("s = " + s);
    }

    @Test
    void redissonTest() throws InterruptedException {
        RLock lock = redissonClient.getLock("my-lock");
        lock.lock();

        Thread.sleep(20000L);

        lock.unlock();
    }

    @Test
    void cacheTest() {
        CacheAutoConfiguration bean = applicationContext.getBean(CacheAutoConfiguration.class);
        System.out.println("bean = " + bean);
        System.out.println("RedisCacheManager.class = " + RedisCacheManager.class);
    }
}
