package top.integer.gulimall.ware.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import top.integer.common.utils.R;

@Component
@FeignClient("gulimall-order")
public interface OrderFeign {
    @GetMapping("order/order/infos/order/{sn}")
    R getOrderInfo(@PathVariable("sn") String sn);
}
