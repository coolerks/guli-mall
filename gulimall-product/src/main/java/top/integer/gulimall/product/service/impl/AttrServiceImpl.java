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
import org.springframework.transaction.annotation.Transactional;
import top.integer.common.utils.PageUtils;
import top.integer.common.utils.Query;

import top.integer.gulimall.product.dao.AttrAttrgroupRelationDao;
import top.integer.gulimall.product.dao.AttrDao;
import top.integer.gulimall.product.dao.AttrGroupDao;
import top.integer.gulimall.product.dao.CategoryDao;
import top.integer.gulimall.product.entity.AttrAttrgroupRelationEntity;
import top.integer.gulimall.product.entity.AttrEntity;
import top.integer.gulimall.product.entity.AttrGroupEntity;
import top.integer.gulimall.product.entity.CategoryEntity;
import top.integer.gulimall.product.service.AttrService;
import top.integer.gulimall.product.vo.AttrRespVo;
import top.integer.gulimall.product.vo.AttrVo;


@Service("attrService")
@Transactional
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    private AttrAttrgroupRelationDao attrAttrgroupRelationDao;
    @Autowired
    private AttrGroupDao attrGroupDao;
    @Autowired
    private CategoryDao categoryDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveAttr(AttrVo attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);
        this.save(attrEntity);
        AttrAttrgroupRelationEntity relation = new AttrAttrgroupRelationEntity();
        relation.setAttrGroupId(attr.getAttrGroupId());
        relation.setAttrId(attrEntity.getAttrId());

        attrAttrgroupRelationDao.insert(relation);
    }

    @Override
    public PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId) {
        LambdaQueryWrapper<AttrEntity> queryWrapper = new LambdaQueryWrapper<>();
        String key = (String) params.get("key");
        queryWrapper.eq(catelogId != 0, AttrEntity::getCatelogId, catelogId)
                .and(StringUtils.isNotEmpty(key),
                        q -> q.eq(AttrEntity::getAttrId, key)
                                .or()
                                .like(AttrEntity::getAttrName, key));
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), queryWrapper);
        List<AttrRespVo> attrRespVos = page.getRecords().stream().map(it -> {
            AttrRespVo attrRespVo = new AttrRespVo();
            BeanUtils.copyProperties(it, attrRespVo);

            AttrAttrgroupRelationEntity attrgroupRelationEntity = attrAttrgroupRelationDao.selectOne(new LambdaUpdateWrapper<AttrAttrgroupRelationEntity>().eq(AttrAttrgroupRelationEntity::getAttrId, it.getAttrId()));
            if (attrgroupRelationEntity != null) {
                AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrgroupRelationEntity.getAttrId());
                if (attrGroupEntity != null) {
                    attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }

            CategoryEntity category = categoryDao.selectById(it.getCatelogId());
            if (category != null) {
                attrRespVo.setCatelogName(category.getName());
            }
            return attrRespVo;
        }).toList();

        PageUtils pageUtils = new PageUtils(page);
        pageUtils.setList(attrRespVos);
        return pageUtils;
    }

}
