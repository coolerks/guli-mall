package top.integer.gulimall.ware.dao;

import org.apache.ibatis.annotations.Param;
import top.integer.gulimall.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.integer.gulimall.ware.vo.StockVo;

import java.util.List;

/**
 * 商品库存
 *
 * @author songxiaoxu
 * @email songxiaoxu2002@gmail.com
 * @date 2023-05-15 15:58:50
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {

    void addStock(Long skuId, Long wareId, Integer skuNum);

    List<StockVo> listStock(@Param("list") List<Long> list);

    Integer lockStock(@Param("id") Long id, @Param("stock") Integer stock);
}
