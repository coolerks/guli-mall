package top.integer.common.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class UserInfoVo {
    private String openid;
    private String nickname;
    private Integer sex;
    private String language;
    private String city;
    private String province;
    private String country;
    private String headimgurl;
    private List<String> privilege;
    private String unionid;
}
