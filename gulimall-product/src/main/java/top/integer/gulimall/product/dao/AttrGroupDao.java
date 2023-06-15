package top.integer.gulimall.product.dao;

import org.apache.ibatis.annotations.Param;
import top.integer.gulimall.product.entity.AttrGroupEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.integer.gulimall.product.vo.SkuItemVo;

import java.util.List;

/**
 * 属性分组
 *
 * @author songxiaoxu
 * @email songxiaoxu2002@gmail.com
 * @date 2023-05-15 14:57:34
 */
@Mapper
public interface AttrGroupDao extends BaseMapper<AttrGroupEntity> {

    List<SkuItemVo.SpuItemAttrGroupVo> getAttrGroupWithAttrsBySpuIdAndCatalogId(@Param("spuId") Long spuId, @Param("catalogId") Long catalogId);
}
