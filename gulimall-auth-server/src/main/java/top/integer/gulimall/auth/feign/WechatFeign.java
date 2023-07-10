package top.integer.gulimall.auth.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import top.integer.gulimall.auth.vo.AccessTokenQueryVo;
import top.integer.gulimall.auth.vo.AccessTokenVo;

@FeignClient(url = "https://api.weixin.qq.com/sns/oauth2", name = "https://api.weixin.qq.com/sns/oauth2")
@Component
public interface WechatFeign {
    @GetMapping("/access_token")
    AccessTokenVo getAccessToken(@RequestParam String appid, @RequestParam String secret, @RequestParam String code,
                                 @RequestParam String grant_type);
}
