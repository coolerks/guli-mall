package top.integer.gulimall.auth.vo;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserLoginVo {
    private String username;
    private String password;
}
