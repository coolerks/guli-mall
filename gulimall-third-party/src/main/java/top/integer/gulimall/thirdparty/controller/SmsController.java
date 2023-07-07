package top.integer.gulimall.thirdparty.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.integer.common.utils.R;
import top.integer.gulimall.thirdparty.component.SmsComponent;

@RestController
@RequestMapping("/sms")
public class SmsController {
    @Autowired
    private SmsComponent smsComponent;
    @GetMapping("")
    public R sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code) {
        smsComponent.sendCode(phone, code);
        return R.ok();
    }
}
