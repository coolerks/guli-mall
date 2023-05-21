package top.integer.gulimall.product.dao;

import org.apache.ibatis.annotations.Param;
import top.integer.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.integer.gulimall.product.vo.AttrGroupRelationVo;

import java.util.List;

/**
 * 属性&属性分组关联
 *
 * @author songxiaoxu
 * @email songxiaoxu2002@gmail.com
 * @date 2023-05-15 14:57:34
 */
@Mapper
public interface AttrAttrgroupRelationDao extends BaseMapper<AttrAttrgroupRelationEntity> {

    void deleteBatchRelation(@Param("list") List<AttrGroupRelationVo> list);
}
