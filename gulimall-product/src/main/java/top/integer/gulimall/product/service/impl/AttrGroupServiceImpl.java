package top.integer.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.w3c.dom.Attr;
import top.integer.common.utils.PageUtils;
import top.integer.common.utils.Query;

import top.integer.gulimall.product.dao.AttrAttrgroupRelationDao;
import top.integer.gulimall.product.dao.AttrGroupDao;
import top.integer.gulimall.product.entity.AttrAttrgroupRelationEntity;
import top.integer.gulimall.product.entity.AttrEntity;
import top.integer.gulimall.product.entity.AttrGroupEntity;
import top.integer.gulimall.product.service.AttrGroupService;
import top.integer.gulimall.product.service.AttrService;
import top.integer.gulimall.product.service.CategoryService;
import top.integer.gulimall.product.service.ProductAttrValueService;
import top.integer.gulimall.product.vo.AttrGroupWithAttrsVo;
import top.integer.gulimall.product.vo.SkuItemVo;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AttrService attrService;

    @Autowired
    private ProductAttrValueService productAttrValueService;



    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catalogId) {
        LambdaQueryWrapper<AttrGroupEntity> attrGroupEntityLambdaQueryWrapper = new LambdaQueryWrapper<>();
        Object key = params.get("key");
        attrGroupEntityLambdaQueryWrapper.eq(catalogId != 0, AttrGroupEntity::getCatelogId, catalogId)
                .and(StringUtils.isNotEmpty((String) key), q -> q.eq(AttrGroupEntity::getAttrGroupId, key)
                        .or().like(AttrGroupEntity::getAttrGroupName, "%" + key + "%")
                );

        IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params),
                attrGroupEntityLambdaQueryWrapper);
        return new PageUtils(page);
    }

    @Override
    public AttrGroupEntity getInfoById(Long attrGroupId) {
        AttrGroupEntity attrGroup = this.getById(attrGroupId);
        attrGroup.setCatelogPath(categoryService.getCatelogPath(attrGroup.getCatelogId()));
        return attrGroup;
    }

    @Override
    public List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCatelogId(Long catelogId) {
        LambdaQueryWrapper<AttrGroupEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AttrGroupEntity::getCatelogId, catelogId);
        List<AttrGroupEntity> list = this.list(queryWrapper);
        return list.stream().map(it -> {
            AttrGroupWithAttrsVo attrGroupWithAttrsVo = new AttrGroupWithAttrsVo();
            BeanUtils.copyProperties(it, attrGroupWithAttrsVo);
            List<AttrEntity> attrs = attrService.getAttrRelation(String.valueOf(it.getAttrGroupId()));
            attrGroupWithAttrsVo.setAttrs(attrs);
            return attrGroupWithAttrsVo;
        }).toList();
    }

    @Override
    public List<SkuItemVo.SpuItemAttrGroupVo> getAttrgroupWithAttrsBySpuId(Long spuId, Long catalogId) {
        return baseMapper.getAttrGroupWithAttrsBySpuIdAndCatalogId(spuId, catalogId);
    }

}
