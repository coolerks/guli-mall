package top.integer.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.integer.common.utils.PageUtils;
import top.integer.gulimall.member.entity.MemberEntity;
import top.integer.gulimall.member.vo.UserLoginVo;
import top.integer.gulimall.member.vo.UserRegistVo;

import java.util.Map;

/**
 * 会员
 *
 * @author songxiaoxu
 * @email songxiaoxu2002@gmail.com
 * @date 2023-05-15 15:41:27
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void regist(UserRegistVo userRegistVo) throws RuntimeException;

    MemberEntity login(UserLoginVo userLoginVo);
}

