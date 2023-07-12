package top.integer.gulimall.product;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.PutObjectResult;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import top.integer.gulimall.product.service.AttrService;

import java.io.File;
import java.util.Optional;
import java.util.ServiceLoader;

@SpringBootApplication
@MapperScan("top.integer.gulimall.product.dao")
@EnableDiscoveryClient
@EnableFeignClients
@EnableCaching
@EnableRedisHttpSession
@ComponentScan("top.integer")
public class GulimallProductApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext run = SpringApplication.run(GulimallProductApplication.class, args);
	}

}
