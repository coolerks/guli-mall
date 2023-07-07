package top.integer.gulimall.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.Md5Crypt;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import top.integer.common.utils.PageUtils;
import top.integer.common.utils.Query;

import top.integer.gulimall.member.dao.MemberDao;
import top.integer.gulimall.member.dao.MemberLevelDao;
import top.integer.gulimall.member.entity.MemberEntity;
import top.integer.gulimall.member.entity.MemberLevelEntity;
import top.integer.gulimall.member.service.MemberService;
import top.integer.gulimall.member.vo.UserLoginVo;
import top.integer.gulimall.member.vo.UserRegistVo;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Autowired
    private MemberLevelDao memberLevelDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void regist(UserRegistVo userRegistVo) throws RuntimeException {
        MemberEntity memberEntity = new MemberEntity();
        BeanUtils.copyProperties(userRegistVo, memberEntity);
        // 检查手机号和用户名是否唯一
        boolean exists = baseMapper.exists(new LambdaQueryWrapper<MemberEntity>()
                .eq(MemberEntity::getMobile, memberEntity.getMobile())
                .or()
                .eq(MemberEntity::getUsername, memberEntity.getUsername())
        );
        if (exists) {
            throw new RuntimeException("手机号或者用户名已存在");
        }
        // 密码加密
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        memberEntity.setPassword(passwordEncoder.encode(memberEntity.getPassword()));

        memberEntity.setMobile(userRegistVo.getPhone());
        MemberLevelEntity memberLevelEntity = memberLevelDao.selectOne(new LambdaQueryWrapper<MemberLevelEntity>()
                .eq(MemberLevelEntity::getDefaultStatus, 1));
        // 设置默认等级
        memberEntity.setLevelId(memberLevelEntity.getId());
        baseMapper.insert(memberEntity);
    }

    @Override
    public MemberEntity login(UserLoginVo userLoginVo) {
        MemberEntity memberEntity = baseMapper.selectOne(new LambdaQueryWrapper<MemberEntity>()
                .eq(MemberEntity::getUsername, userLoginVo.getUsername())
                .or()
                .eq(MemberEntity::getMobile, userLoginVo.getUsername())
        );
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (memberEntity == null || !passwordEncoder.matches(userLoginVo.getPassword(), memberEntity.getPassword())) {
            return null;
        }
        return memberEntity;
    }

}
