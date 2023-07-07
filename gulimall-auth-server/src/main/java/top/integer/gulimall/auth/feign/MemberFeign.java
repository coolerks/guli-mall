package top.integer.gulimall.auth.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import top.integer.common.utils.R;
import top.integer.gulimall.auth.vo.UserLoginVo;
import top.integer.gulimall.auth.vo.UserRegistVo;

@Component
@FeignClient("gulimall-member")
public interface MemberFeign {
    @PostMapping("/member/member/regist")
    R regist(@RequestBody UserRegistVo vo);

    @PostMapping("/member/member/login")
    R login(@RequestBody UserLoginVo vo);
}
