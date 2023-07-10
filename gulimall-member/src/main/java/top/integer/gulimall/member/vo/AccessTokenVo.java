package top.integer.gulimall.member.vo;

import lombok.Data;
import lombok.ToString;
import org.codehaus.jackson.annotate.JsonProperty;

@Data
@ToString
public class AccessTokenVo {
    @JsonProperty("access_token")
    private String access_token;
    @JsonProperty("refresh_token")
    private String refresh_token;
    private String openid, scope, unionid;
    private Integer expires_in;
}
