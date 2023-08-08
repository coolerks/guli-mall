package top.integer.gulimall.seckill.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.integer.common.utils.R;

@RestController
@RequestMapping("/test")
public class TestController {
    @GetMapping("/test")
    public R test() {
        return R.ok();
    }
}
