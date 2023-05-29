package top.integer.gulimall.ware.vo;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@ToString
public class PurchaseDoneVo {

    @NotNull(message = "id不能为空")
    private Long id;
    private List<PurchaseDoneVoItem> items;

    @Data
    @ToString
    public static class PurchaseDoneVoItem {
        @NotNull(message = "id不能为空")
        private Long itemId;
        @NotNull(message = "状态不能为空")
        private Integer status;
        private String reason;
    }
}
