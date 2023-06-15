package top.integer.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import top.integer.common.utils.PageUtils;
import top.integer.common.utils.Query;

import top.integer.gulimall.product.constant.ProductContant;
import top.integer.gulimall.product.dao.AttrAttrgroupRelationDao;
import top.integer.gulimall.product.dao.AttrDao;
import top.integer.gulimall.product.dao.AttrGroupDao;
import top.integer.gulimall.product.dao.CategoryDao;
import top.integer.gulimall.product.entity.AttrAttrgroupRelationEntity;
import top.integer.gulimall.product.entity.AttrEntity;
import top.integer.gulimall.product.entity.AttrGroupEntity;
import top.integer.gulimall.product.entity.CategoryEntity;
import top.integer.gulimall.product.service.AttrService;
import top.integer.gulimall.product.service.CategoryService;
import top.integer.gulimall.product.vo.AttrGroupRelationVo;
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

    @Autowired
    private CategoryService categoryService;


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
        if (attr.getAttrType() == ProductContant.AttrEnum.ATTR_TYPE_BASE.getCode() && attr.getAttrGroupId() != null) {
            AttrAttrgroupRelationEntity relation = new AttrAttrgroupRelationEntity();
            relation.setAttrGroupId(attr.getAttrGroupId());
            relation.setAttrId(attrEntity.getAttrId());
            attrAttrgroupRelationDao.insert(relation);
        }
    }

    @Override
    public PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String type) {
        LambdaQueryWrapper<AttrEntity> queryWrapper = new LambdaQueryWrapper<>();
        String key = (String) params.get("key");

        queryWrapper.eq(catelogId != 0, AttrEntity::getCatelogId, catelogId)
                .eq("sale".equals(type), AttrEntity::getAttrType, 0)
                .eq("base".equals(type), AttrEntity::getAttrType, 1)
                .and(StringUtils.isNotEmpty(key),
                        q -> q.eq(AttrEntity::getAttrId, key)
                                .or()
                                .like(AttrEntity::getAttrName, key));

        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), queryWrapper);
        List<AttrRespVo> attrRespVos = page.getRecords().stream().map(it -> {
            AttrRespVo attrRespVo = new AttrRespVo();
            BeanUtils.copyProperties(it, attrRespVo);

            AttrAttrgroupRelationEntity attrgroupRelationEntity = attrAttrgroupRelationDao
                    .selectOne(new LambdaUpdateWrapper<AttrAttrgroupRelationEntity>().eq(AttrAttrgroupRelationEntity::getAttrId, it.getAttrId()));
            if (attrgroupRelationEntity != null) {
                AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrgroupRelationEntity.getAttrGroupId());
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

    @Override
    public AttrRespVo getAttrInfo(Long attrId) {
        AttrEntity attrEntity = this.getById(attrId);
        AttrRespVo attr = new AttrRespVo();
        BeanUtils.copyProperties(attrEntity, attr);
        attr.setCatelogPath(categoryService.getCatelogPath(attrEntity.getCatelogId()));
        if (attr.getAttrType() == ProductContant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = attrAttrgroupRelationDao.selectOne(new LambdaUpdateWrapper<AttrAttrgroupRelationEntity>().eq(AttrAttrgroupRelationEntity::getAttrId, attrEntity.getAttrId()));

            if (attrAttrgroupRelationEntity != null) {
                AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrAttrgroupRelationEntity.getAttrGroupId());
                if (attrGroupEntity != null) {
                    attr.setAttrGroupId(attrAttrgroupRelationEntity.getAttrGroupId());
                    attrGroupEntity.setAttrGroupName(attrGroupEntity.getAttrGroupName());
                }
            }
        }
        CategoryEntity categoryEntity = categoryDao.selectById(attrEntity.getCatelogId());
        if (categoryEntity != null) {
            attr.setCatelogName(categoryEntity.getName());
        }
        return attr;
    }

    @Override
    public void updateAttr(AttrVo attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);
        this.updateById(attrEntity);
        if (attr.getAttrType() == ProductContant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            Long count = attrAttrgroupRelationDao.selectCount(new LambdaQueryWrapper<AttrAttrgroupRelationEntity>()
                    .eq(AttrAttrgroupRelationEntity::getAttrId, attr.getAttrId()));
            if (count > 0) {
                LambdaUpdateWrapper<AttrAttrgroupRelationEntity> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(AttrAttrgroupRelationEntity::getAttrId, attrEntity.getAttrId())
                        .set(AttrAttrgroupRelationEntity::getAttrGroupId, attr.getAttrGroupId());
                attrAttrgroupRelationDao.update(null, updateWrapper);
            } else {
                AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
                attrAttrgroupRelationEntity.setAttrId(attr.getAttrId());
                attrAttrgroupRelationEntity.setAttrGroupId(attr.getAttrGroupId());
                attrAttrgroupRelationDao.insert(attrAttrgroupRelationEntity);
            }
        }
    }

    @Override
    public List<AttrEntity> getAttrRelation(String attrgroupId) {
        List<AttrAttrgroupRelationEntity> relationList = attrAttrgroupRelationDao.selectList(new LambdaUpdateWrapper<AttrAttrgroupRelationEntity>()
                .eq(AttrAttrgroupRelationEntity::getAttrGroupId, attrgroupId));
        if (relationList != null) {
            List<Long> attrIdList = relationList.stream().map(AttrAttrgroupRelationEntity::getAttrId).toList();
            if (attrIdList.size() > 0) {
                return this.listByIds(attrIdList);
            }
        }
        return Collections.emptyList();
    }

    @Override
    public void deleteRelation(List<AttrGroupRelationVo> list) {
        attrAttrgroupRelationDao.deleteBatchRelation(list);
    }

    @Override
    public PageUtils getNoAttrReleation(Map<String, Object> params, Long attrgroupId) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        String key = (String) params.get("key");

        LambdaUpdateWrapper<AttrGroupEntity> queryWrapper = new LambdaUpdateWrapper<>();
        queryWrapper.eq(AttrGroupEntity::getAttrGroupId, attrgroupId);
        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrgroupId);

        if (attrGroupEntity != null) {
            Long catelogId = attrGroupEntity.getCatelogId();
            List<AttrAttrgroupRelationEntity> existedRelation = attrAttrgroupRelationDao
                    .selectList(new LambdaQueryWrapper<AttrAttrgroupRelationEntity>()
                    .eq(AttrAttrgroupRelationEntity::getAttrGroupId, attrgroupId));

            LambdaQueryWrapper<AttrEntity> attrQueryWrapper = new LambdaQueryWrapper<>();
            attrQueryWrapper.eq(AttrEntity::getCatelogId, catelogId)
                    .and(
                            StringUtils.isNotBlank(key), s -> s.like(AttrEntity::getAttrName, "%" + key + "%")
                            .or()
                            .eq(AttrEntity::getAttrId, key)
                    )
                    .eq(AttrEntity::getAttrType, ProductContant.AttrEnum.ATTR_TYPE_BASE.getCode())
                    .notIn(existedRelation != null && !existedRelation.isEmpty(),
                            AttrEntity::getAttrId, existedRelation.stream().map(AttrAttrgroupRelationEntity::getAttrId).toList());

            return new PageUtils(this.page(page, attrQueryWrapper));
        }
        return new PageUtils(Collections.emptyList(), 0, 0, 0);
    }

    @Override
    public Set<Long> getAttrIdsAllowedToSearch(List<Long> attrIds) {
        LambdaQueryWrapper<AttrEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(AttrEntity::getAttrId, attrIds)
                .eq(AttrEntity::getSearchType, 1);
        return this.list(queryWrapper).stream().map(AttrEntity::getAttrId).collect(Collectors.toSet());
    }

}
