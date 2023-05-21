package top.integer.gulimall.product.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class AttrRespVo extends AttrVo {
    private String catelogName;
    private String groupName;

    private List<Long> catelogPath;
}
