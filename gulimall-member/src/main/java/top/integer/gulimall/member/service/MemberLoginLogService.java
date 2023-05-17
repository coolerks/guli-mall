package top.integer.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.integer.common.utils.PageUtils;
import top.integer.gulimall.member.entity.MemberLoginLogEntity;

import java.util.Map;

/**
 * 会员登录记录
 *
 * @author songxiaoxu
 * @email songxiaoxu2002@gmail.com
 * @date 2023-05-15 15:41:27
 */
public interface MemberLoginLogService extends IService<MemberLoginLogEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

