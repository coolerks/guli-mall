package top.integer.gulimall.product.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import top.integer.common.to.es.Product;
import top.integer.common.utils.R;

import java.util.List;

@Component
@FeignClient("gulimall-search")
public interface SearchFeign {
    @PostMapping("/search/save/product")
    R save(@RequestBody List<Product> list);
}
