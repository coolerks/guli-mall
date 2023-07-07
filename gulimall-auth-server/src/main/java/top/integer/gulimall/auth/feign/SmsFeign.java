package top.integer.gulimall.auth.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import top.integer.common.utils.R;

@Component
@FeignClient("gulimall-third-party")
public interface SmsFeign {
    @GetMapping("/sms")
    R sendCode(@RequestParam("phone") String phone);
}
