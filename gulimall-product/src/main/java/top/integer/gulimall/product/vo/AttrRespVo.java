package top.integer.gulimall.product.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class AttrRespVo extends AttrVo {
    private String catelogName;
    private String groupName;
}
