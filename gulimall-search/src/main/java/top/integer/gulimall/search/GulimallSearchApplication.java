package top.integer.gulimall.search;

import com.alibaba.alicloud.context.oss.OssContextAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(exclude = {OssContextAutoConfiguration.class, DataSourceAutoConfiguration.class})
@EnableDiscoveryClient
public class GulimallSearchApplication {

	public static void main(String[] args) {
		SpringApplication.run(GulimallSearchApplication.class, args);
	}

}
