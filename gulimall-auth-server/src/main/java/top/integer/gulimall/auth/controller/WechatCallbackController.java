package top.integer.gulimall.auth.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import top.integer.common.utils.R;
import top.integer.common.vo.MemberEntity;
import top.integer.gulimall.auth.feign.MemberFeign;
import top.integer.gulimall.auth.feign.WechatFeign;
import top.integer.gulimall.auth.properties.WechatProperties;
import top.integer.gulimall.auth.vo.AccessTokenQueryVo;
import top.integer.gulimall.auth.vo.AccessTokenVo;
import top.integer.gulimall.auth.vo.WechatLoginVo;

import javax.servlet.http.HttpSession;

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
        return "redirect:http://auth.gulimall.com/wechat/callback?" +
                "code=" + wechatLoginVo.getCode() + "&" +
                "state=" + wechatLoginVo.getState();
    }

    @GetMapping("/wechat/callback")
    public String callBack(WechatLoginVo wechatLoginVo, HttpSession session) {
        try {
            AccessTokenQueryVo query = new AccessTokenQueryVo();
            query.setCode(wechatLoginVo.getCode());
            query.setGrant_type("authorization_code");
            query.setAppid(wechatProperties.getAppId());
            query.setSecret(wechatProperties.getAppSecret());
            AccessTokenVo accessToken = wechatFeign.getAccessToken(query.getAppid(), query.getSecret(),
                    query.getCode(), query.getGrant_type());
            R r = memberFeign.oauth2(accessToken);
            if (r.getCode() != 0) {
                throw new RuntimeException();
            }

            MemberEntity data = r.getData(new TypeReference<MemberEntity>() {
            });
            session.setAttribute("loginUser", data);
            System.out.println("session0.getId() = " + session.getId());

        } catch (Exception e) {
            return "redirect:http://auth.gulimall.com/login.html";
        }
        return "redirect:http://gulimall.com";
    }

//
//    @GetMapping("/")
//    @ResponseBody
//    public void sess(HttpSession session) {
//        System.out.println("session.getId() = " + session.getId());
//        Object loginUser = session.getAttribute("loginUser");
//        System.out.println("loginUser = " + loginUser);
//    }
//
//    @GetMapping("/second")
//    @ResponseBody
//    public void sess2(HttpSession session) {
//        System.out.println("session2.getId() = " + session.getId());
//        Object loginUser = session.getAttribute("loginUser");
//        System.out.println("loginUser2 = " + loginUser);
//    }
}
