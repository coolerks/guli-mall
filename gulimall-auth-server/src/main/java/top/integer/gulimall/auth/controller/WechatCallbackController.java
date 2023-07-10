package top.integer.gulimall.auth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import top.integer.common.utils.R;
import top.integer.gulimall.auth.feign.MemberFeign;
import top.integer.gulimall.auth.feign.UserInfoFeign;
import top.integer.gulimall.auth.feign.WechatFeign;
import top.integer.gulimall.auth.properties.WechatProperties;
import top.integer.gulimall.auth.vo.AccessTokenQueryVo;
import top.integer.gulimall.auth.vo.AccessTokenVo;
import top.integer.gulimall.auth.vo.UserInfoVo;
import top.integer.gulimall.auth.vo.WechatLoginVo;

@Controller
@RefreshScope
public class WechatCallbackController {
    @Autowired
    private WechatFeign wechatFeign;
    @Autowired
    private MemberFeign memberFeign;
    @Autowired
    private WechatProperties wechatProperties;
    @GetMapping("/api/user/wx/callback")
    public String callBack(WechatLoginVo wechatLoginVo) {
        try {
            AccessTokenQueryVo query = new AccessTokenQueryVo();
            query.setCode(wechatLoginVo.getCode());
            query.setGrant_type("authorization_code");
            query.setAppid(wechatProperties.getAppId());
            query.setSecret(wechatProperties.getAppSecret());
            AccessTokenVo accessToken = wechatFeign.getAccessToken(query.getAppid(), query.getSecret(),
                    query.getCode(), query.getGrant_type());
            System.out.println("accessToken = " + accessToken);
            R r = memberFeign.oauth2(accessToken);
            if (r.getCode() != 0) {
                throw new RuntimeException();
            }

        } catch (Exception e) {
            return "redirect:http://auth.gulimall.com/login.html";
        }
        return "redirect:http://gulimall.com";
    }
}
