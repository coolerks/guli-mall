package top.integer.gulimall.cart.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import top.integer.common.utils.R;

import java.util.Map;

@FeignClient("gulimall-product")
@Component
public interface SkuFeign {
    @GetMapping("/product/skuinfo/info/{skuId}")
    R getSkuInfo(@PathVariable("skuId") Long skuId);

    @GetMapping("/product/skusaleattrvalue/stringlist/{skuId}")
    R getAttrValues(@PathVariable("skuId") Long skuId);
}
