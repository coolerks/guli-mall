package top.integer.gulimall.thirdparty.properties;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.cloud.alicloud.sms")
@Data
@ToString
public class SmsProperties {
    private String appcode;
    private String templateId = "CST_ptdie100";
}
