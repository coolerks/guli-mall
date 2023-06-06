package top.integer.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.integer.common.utils.PageUtils;
import top.integer.gulimall.product.entity.SkuInfoEntity;

import java.util.List;
import java.util.Map;

/**
 * sku信息
 *
 * @author songxiaoxu
 * @email songxiaoxu2002@gmail.com
 * @date 2023-05-15 14:57:34
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<SkuInfoEntity> getBySpuId(Long spuId);
}

