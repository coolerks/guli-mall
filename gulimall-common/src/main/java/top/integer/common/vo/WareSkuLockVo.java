package top.integer.common.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import top.integer.common.to.LockStockTo;

import java.util.List;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class WareSkuLockVo {
    private List<LockStockTo> list;
    private String orderSn;
}
