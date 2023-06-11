package top.integer.gulimall.search.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import top.integer.common.utils.R;

@FeignClient("gulimall-product")
@Component
public interface ProductFeign {
   @GetMapping("product/attr/info/{attrId}")
    R info(@PathVariable("attrId") Long attrId);
}
