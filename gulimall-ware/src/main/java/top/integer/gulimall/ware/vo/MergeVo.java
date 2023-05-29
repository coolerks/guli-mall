package top.integer.gulimall.ware.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class MergeVo {
    private Long purchaseId;
    private List<Long> items;
}
