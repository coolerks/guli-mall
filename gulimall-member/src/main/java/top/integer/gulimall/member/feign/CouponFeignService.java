package top.integer.gulimall.member.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import top.integer.common.utils.R;

@FeignClient(value = "gulimall-coupon", name = "gulimall-coupon")
@Component
public interface CouponFeignService {
    @RequestMapping("/coupon/coupon/member/list")
    R memberCoupon();
}
