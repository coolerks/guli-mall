package top.integer.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.w3c.dom.Attr;
import top.integer.common.utils.PageUtils;
import top.integer.common.utils.Query;

import top.integer.gulimall.product.dao.AttrGroupDao;
import top.integer.gulimall.product.entity.AttrGroupEntity;
import top.integer.gulimall.product.service.AttrGroupService;
import top.integer.gulimall.product.service.CategoryService;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    private CategoryService categoryService;

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

}
