package top.integer.gulimall.auth.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import top.integer.common.exception.BizCodeEnume;
import top.integer.common.utils.R;
import top.integer.common.vo.MemberEntity;
import top.integer.gulimall.auth.feign.MemberFeign;
import top.integer.gulimall.auth.feign.SmsFeign;
import top.integer.gulimall.auth.vo.UserLoginVo;
import top.integer.gulimall.auth.vo.UserRegistVo;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/")
@Slf4j
public class LoginController {
    @Autowired
    private SmsFeign feign;
    @Autowired
    private MemberFeign memberFeign;
    @Autowired
    private StringRedisTemplate template;

    private static final Random RANDOM = new Random();

    @GetMapping("/sms/sendCode")
    @ResponseBody
    public R sendCode(@RequestParam("phone") String phone) {
        String key = "sms:code:" + phone;
        String value = template.opsForValue().get(key);
        if (StringUtils.isNotBlank(value)) {
            String[] split = value.split("_");
            if (split.length == 2) {
                long time = Long.parseLong(split[1]);
                if (time + 60000 > System.currentTimeMillis()) {
                    return R.error(BizCodeEnume.SMS_CODE_EXCEPTION.getCode(), BizCodeEnume.SMS_CODE_EXCEPTION.getMsg());
                }
            }
        }
        String code = String.valueOf(RANDOM.nextInt(100000, 999999));
        template.opsForValue().set(key, code + "_" + System.currentTimeMillis(), 5, TimeUnit.MINUTES);
        log.info("手机号为：{}，验证码为：{}", phone, code);
        System.out.println("feign = " + feign);
        return R.ok();
    }

    @PostMapping("/login")
    public String login(UserLoginVo userLoginVo, HttpSession session) {
        try {
            R result = memberFeign.login(userLoginVo);
            if (result.getCode() != 0) {
                return "redirect:http://auth.gulimall.com/login.html";
            }
            MemberEntity member = result.getData(new TypeReference<MemberEntity>() {
            });
            session.setAttribute("loginUser", member);
        } catch (Exception e) {
            return "redirect:http://auth.gulimall.com/login.html";
        }
        return "redirect:http://gulimall.com";
    }

    @PostMapping("/regist")
    public String regist(@Validated UserRegistVo userRegistVo, BindingResult result, RedirectAttributes model) {
        System.out.println("userRegistVo = " + userRegistVo);
        if (result.hasErrors()) {
            Map<String, String> errors = result.getFieldErrors().stream()
                    .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            model.addFlashAttribute("errors", errors);
            System.out.println("errors = " + errors);
            return "redirect:http://auth.gulimall.com/reg.html";
        }
        String key = "sms:code:" + userRegistVo.getPhone();
        String code = userRegistVo.getCode();
        String value = template.opsForValue().get(key);
        if (StringUtils.isNotBlank(value)) {
            String[] split = value.split("_");
            if (split.length == 2) {
                String redisCode = split[0];
                if (!code.equals(redisCode)) {
                    model.addFlashAttribute("errors", Map.of("code", "验证码不正确"));
                    return "redirect:http://auth.gulimall.com/reg.html";
                }
            } else {
                model.addFlashAttribute("errors", Map.of("code", "验证码不正确"));
                return "redirect:http://auth.gulimall.com/reg.html";
            }
            template.delete(key);
        } else {
            model.addFlashAttribute("errors", Map.of("code", "验证码不正确"));
            return "redirect:http://auth.gulimall.com/reg.html";
        }
        R r = memberFeign.regist(userRegistVo);
        System.out.println("r = " + r);
        if (r.getCode() != 0) {
            return "redirect:http://auth.gulimall.com/reg.html";
        }
        return "redirect:http://auth.gulimall.com/login.html";
    }

}
