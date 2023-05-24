package top.integer.gulimall.product;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.PutObjectResult;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.File;

@SpringBootApplication
@MapperScan("top.integer.gulimall.product.dao")
@EnableDiscoveryClient
@EnableFeignClients
public class GulimallProductApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext run = SpringApplication.run(GulimallProductApplication.class, args);
	}

}
