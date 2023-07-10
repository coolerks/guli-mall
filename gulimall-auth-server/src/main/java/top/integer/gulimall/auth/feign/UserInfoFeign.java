package top.integer.gulimall.auth.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import top.integer.gulimall.auth.vo.UserInfoVo;

@FeignClient(url = "https://api.weixin.qq.com/sns", name = "https://api.weixin.qq.com/sns/userinfo")
@Component
public interface UserInfoFeign {
    @GetMapping("/userinfo")
    UserInfoVo getUserInfo(@RequestParam String access_token, @RequestParam String openid);
}
