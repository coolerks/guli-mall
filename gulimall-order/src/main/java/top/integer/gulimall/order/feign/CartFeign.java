package top.integer.gulimall.order.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import top.integer.gulimall.order.vo.OrderItemVo;

import java.util.List;

@Component
@FeignClient("gulimall-cart")
public interface CartFeign {
    @GetMapping("/currentUserCartItems")
    List<OrderItemVo> getCurrentUserCartItems();
}
