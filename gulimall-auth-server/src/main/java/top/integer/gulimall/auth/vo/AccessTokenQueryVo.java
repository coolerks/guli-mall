package top.integer.gulimall.auth.vo;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AccessTokenQueryVo {
    private String appid, secret, code, grant_type;
}
