package top.integer.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;
import top.integer.common.to.LockStockTo;
import top.integer.common.utils.PageUtils;
import top.integer.common.vo.WareSkuLockVo;
import top.integer.gulimall.ware.entity.WareSkuEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author songxiaoxu
 * @email songxiaoxu2002@gmail.com
 * @date 2023-05-15 15:58:50
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void addStock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("skuNum") Integer skuNum);

    Map<Long, Boolean> hasStock(List<Long> skuIds);

    boolean orderLockStock(WareSkuLockVo wareSkuLockVo);
}

