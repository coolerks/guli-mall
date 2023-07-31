package top.integer.gulimall.order.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Component
@FeignClient("gulimall-product")
public interface SkuInfoFeign {
    @PostMapping("/product/skuinfo/price")
    Map<Long, BigDecimal> getSkusPrice(@RequestBody List<Long> ids);
}
