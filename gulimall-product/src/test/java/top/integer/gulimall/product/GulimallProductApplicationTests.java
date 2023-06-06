package top.integer.gulimall.product;

import com.aliyun.oss.OSS;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;
import org.springframework.boot.test.context.SpringBootTest;
import top.integer.common.utils.R;
import top.integer.gulimall.product.entity.BrandEntity;
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

}
