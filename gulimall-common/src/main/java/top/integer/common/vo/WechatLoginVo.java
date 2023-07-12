package top.integer.common.vo;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class WechatLoginVo {
    private String code, state;
}
