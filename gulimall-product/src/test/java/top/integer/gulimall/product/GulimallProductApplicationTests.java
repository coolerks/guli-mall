package top.integer.gulimall.product;

import com.aliyun.oss.OSS;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import top.integer.gulimall.product.entity.BrandEntity;
import top.integer.gulimall.product.service.BrandService;

@SpringBootTest
class GulimallProductApplicationTests {

	@Autowired
	BrandService brandService;

	@Autowired
	private OSS ossClient;
	@Test
	void contextLoads() {
//		System.out.println("brandService = " + brandService);
//		BrandEntity brand = new BrandEntity();
//		brand.setName("华为");
//		brandService.save(brand);
//		System.out.println("brandService.list() = " + brandService.list());
		System.out.println("ossClient = " + ossClient);

	}

}
