package top.integer.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.integer.common.utils.PageUtils;
import top.integer.gulimall.product.entity.AttrEntity;
import top.integer.gulimall.product.vo.AttrGroupRelationVo;
import top.integer.gulimall.product.vo.AttrRespVo;
import top.integer.gulimall.product.vo.AttrVo;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 商品属性
 *
 * @author songxiaoxu
 * @email songxiaoxu2002@gmail.com
 * @date 2023-05-15 14:57:33
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveAttr(AttrVo attr);

    PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String type);

    AttrRespVo getAttrInfo(Long attrId);

    void updateAttr(AttrVo attr);

    List<AttrEntity> getAttrRelation(String attrgroupId);

    void deleteRelation(List<AttrGroupRelationVo> list);

    PageUtils getNoAttrReleation(Map<String, Object> params, Long attrgroupId);

    Set<Long> getAttrIdsAllowedToSearch(List<Long> attrIds);

}

