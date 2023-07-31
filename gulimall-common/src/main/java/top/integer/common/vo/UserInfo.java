package top.integer.common.vo;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserInfo {
    private Long userId;
    private String userKey;
    private boolean tempUser = false;
}
