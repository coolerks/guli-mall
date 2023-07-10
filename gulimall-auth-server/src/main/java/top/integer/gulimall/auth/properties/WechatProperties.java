package top.integer.gulimall.auth.properties;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ToString
@ConfigurationProperties(prefix = "wx.open")
public class WechatProperties {
    private String appId, appSecret, redirectUri, sytBaseUrl;
}
