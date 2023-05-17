package top.integer.gulimall.member.dao;

import top.integer.gulimall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author songxiaoxu
 * @email songxiaoxu2002@gmail.com
 * @date 2023-05-15 15:41:27
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
