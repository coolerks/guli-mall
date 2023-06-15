package top.integer.gulimall.product.dao;

import org.apache.ibatis.annotations.Param;
import top.integer.gulimall.product.entity.SkuSaleAttrValueEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.integer.gulimall.product.vo.SkuItemVo;

import java.util.List;

/**
 * sku销售属性&值
 *
 * @author songxiaoxu
 * @email songxiaoxu2002@gmail.com
 * @date 2023-05-15 14:57:34
 */
@Mapper
public interface SkuSaleAttrValueDao extends BaseMapper<SkuSaleAttrValueEntity> {

    List<SkuItemVo.ItemSaleAttrsVo> getSaleAttrsBySpuId(@Param("spuId") Long spuId);
}
