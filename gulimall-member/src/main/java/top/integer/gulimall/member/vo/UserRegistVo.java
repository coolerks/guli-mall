package top.integer.gulimall.member.vo;

import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

@Data
@ToString
public class UserRegistVo {
    @NotEmpty(message = "用户名不能为空")
    @Length(min = 6, max = 18, message = "用户名必须6-18位")
    private String username;
    @NotEmpty(message = "密码不能为空")
    @Length(min = 6, max = 18, message = "密码必须6-18位")
    private String password;
    @Length(min = 11, max = 11)
    @NotEmpty(message = "手机号不能为空")
    private String phone;
}
