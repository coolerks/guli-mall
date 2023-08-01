package top.integer.gulimall.product.dao;

import top.integer.common.vo.ProductInfoVo;
import top.integer.gulimall.product.entity.SkuInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.integer.gulimall.product.entity.SpuInfoEntity;

import java.util.List;

/**
 * sku信息
 *
 * @author songxiaoxu
 * @email songxiaoxu2002@gmail.com
 * @date 2023-05-15 14:57:34
 */
@Mapper
public interface SkuInfoDao extends BaseMapper<SkuInfoEntity> {

    List<ProductInfoVo> getSpuInfoBySkuIds(List<Long> ids);
}
