package top.integer.gulimall.seckill.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import top.integer.gulimall.seckill.vo.SeckillSessionVo;

import java.util.List;

@FeignClient("gulimall-coupon")
@Component
public interface SeckillFeign {
    @GetMapping("/coupon/seckillsession/latest3days")
    List<SeckillSessionVo> getLastest3DaysSku();
}
