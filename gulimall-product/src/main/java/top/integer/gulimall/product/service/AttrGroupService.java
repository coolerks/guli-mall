package top.integer.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.integer.common.utils.PageUtils;
import top.integer.gulimall.product.entity.AttrEntity;
import top.integer.gulimall.product.entity.AttrGroupEntity;
import top.integer.gulimall.product.vo.AttrGroupWithAttrsVo;
import top.integer.gulimall.product.vo.SkuItemVo;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author songxiaoxu
 * @email songxiaoxu2002@gmail.com
 * @date 2023-05-15 14:57:34
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPage(Map<String, Object> params, Long catalogId);

    AttrGroupEntity getInfoById(Long attrGroupId);

    List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCatelogId(Long catelogId);

    List<SkuItemVo.SpuItemAttrGroupVo> getAttrgroupWithAttrsBySpuId(Long spuId, Long catalogId);
}

