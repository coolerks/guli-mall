package top.integer.gulimall.auth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import top.integer.gulimall.auth.feign.WechatFeign;
import top.integer.gulimall.auth.properties.WechatProperties;
import top.integer.gulimall.auth.vo.AccessTokenQueryVo;
import top.integer.gulimall.auth.vo.AccessTokenVo;

@SpringBootTest
class GulimallAuthServerApplicationTests {
    @Autowired
    WechatFeign wechatFeign;
    @Autowired
    WechatProperties wechatProperties;

    @Test
    void contextLoads() {
    }


    @Test
    void sendCode() {
//        AccessTokenQueryVo query = new AccessTokenQueryVo();
//        query.setCode("021DWnml2BALDb4Rlkml2LhKpR0DWnmg");
//        query.setGrant_type("authorization_code");
//        query.setAppid(wechatProperties.getAppId());
//        query.setSecret(wechatProperties.getAppSecret());
//        AccessTokenVo accessToken = wechatFeign.getAccessToken(query.getAppid(), query.getSecret(),
//                query.getCode(), query.getGrant_type());
//        System.out.println("accessToken = " + accessToken);
    }

}
