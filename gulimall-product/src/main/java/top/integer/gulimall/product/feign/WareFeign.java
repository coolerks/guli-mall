package top.integer.gulimall.product.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import top.integer.common.utils.R;

import java.util.List;
import java.util.Map;

@Component
@FeignClient("gulimall-ware")
public interface WareFeign {
    @PostMapping("/ware/waresku/hasstock")
    R hasStock(@RequestBody List<Long> skuIds);
}
