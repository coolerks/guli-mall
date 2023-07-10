package top.integer.gulimall.member.controller;

import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import top.integer.common.exception.BizCodeEnume;
import top.integer.gulimall.member.entity.MemberEntity;
import top.integer.gulimall.member.feign.CouponFeignService;
import top.integer.gulimall.member.service.MemberService;
import top.integer.common.utils.PageUtils;
import top.integer.common.utils.R;
import top.integer.gulimall.member.vo.AccessTokenVo;
import top.integer.gulimall.member.vo.UserLoginVo;
import top.integer.gulimall.member.vo.UserRegistVo;


/**
 * 会员
 *
 * @author songxiaoxu
 * @email songxiaoxu2002@gmail.com
 * @date 2023-05-15 15:41:27
 */
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;
    @Autowired
    private CouponFeignService couponFeignService;

    @RequestMapping("/coupons")
    public R test() {
        MemberEntity member = new MemberEntity();
        member.setNickname("张三");
        return couponFeignService.memberCoupon().put("member", member);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody MemberEntity member){
		memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody MemberEntity member){
		memberService.updateById(member);

        return R.ok();
    }

    @PostMapping("/regist")
    public R regist(@RequestBody UserRegistVo userRegistVo) {
        try {
            memberService.regist(userRegistVo);
        } catch (RuntimeException e) {
            return R.error(BizCodeEnume.USER_EXIST_EXCEPTION.getCode(), BizCodeEnume.USER_EXIST_EXCEPTION.getMsg());
        }
        return R.ok();
    }

    @PostMapping("/login")
    public R login(@RequestBody UserLoginVo userLoginVo) {
        if (memberService.login(userLoginVo) == null) {
            return R.error(BizCodeEnume.LOGIN_INVALID.getCode(), BizCodeEnume.LOGIN_INVALID.getMsg());
        }
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    @PostMapping("/oauth2/login")
    public R loginOrRegister(@RequestBody AccessTokenVo accessTokenVo) {
        memberService.loginOrRegister(accessTokenVo);
        return R.ok();
    }

}
