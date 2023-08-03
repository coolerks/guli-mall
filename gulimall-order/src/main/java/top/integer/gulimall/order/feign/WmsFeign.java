package top.integer.gulimall.order.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import top.integer.common.to.LockStockTo;
import top.integer.common.utils.R;
import top.integer.common.vo.WareSkuLockVo;

import java.util.List;

@FeignClient("gulimall-ware")
@Component
public interface WmsFeign {
    @PostMapping("/ware/waresku/hasstock")
    R hasStock(@RequestBody List<Long> skuIds);

    @PostMapping("/ware/waresku/lock/order")
    R orderLockStock(@RequestBody WareSkuLockVo wareSkuLockVo);
}
