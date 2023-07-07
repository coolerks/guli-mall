package top.integer.gulimall.thirdparty.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import top.integer.gulimall.thirdparty.properties.SmsProperties;

@Configuration
@EnableConfigurationProperties(SmsProperties.class)
public class SmsConfig {
}
