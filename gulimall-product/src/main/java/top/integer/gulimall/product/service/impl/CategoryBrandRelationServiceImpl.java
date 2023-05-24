package top.integer.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
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

import top.integer.gulimall.product.dao.BrandDao;
import top.integer.gulimall.product.dao.CategoryBrandRelationDao;
import top.integer.gulimall.product.dao.CategoryDao;
import top.integer.gulimall.product.entity.BrandEntity;
import top.integer.gulimall.product.entity.CategoryBrandRelationEntity;
import top.integer.gulimall.product.entity.CategoryEntity;
import top.integer.gulimall.product.service.BrandService;
import top.integer.gulimall.product.service.CategoryBrandRelationService;
import top.integer.gulimall.product.service.CategoryService;
import top.integer.gulimall.product.vo.BrandVo;


@Service("categoryBrandRelationService")
@Transactional
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private BrandDao brandDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),
                new QueryWrapper<CategoryBrandRelationEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveDetail(CategoryBrandRelationEntity categoryBrandRelation) {
        CategoryEntity category = categoryDao.selectById(categoryBrandRelation.getCatelogId());
        BrandEntity brand = brandDao.selectById(categoryBrandRelation.getBrandId());
        categoryBrandRelation.setCatelogName(category.getName());
        categoryBrandRelation.setBrandName(brand.getName());
        this.save(categoryBrandRelation);
    }

    @Override
    public void updateBrand(Long brandId, String name) {
        LambdaUpdateWrapper<CategoryBrandRelationEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(CategoryBrandRelationEntity::getBrandName, name);
        updateWrapper.eq(CategoryBrandRelationEntity::getBrandId, brandId);
        this.update(updateWrapper);
    }

    @Override
    public void updateCategory(Long catId, String name) {
        LambdaUpdateWrapper<CategoryBrandRelationEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(CategoryBrandRelationEntity::getCatelogId, catId)
                .set(CategoryBrandRelationEntity::getCatelogName, name);
        this.update(updateWrapper);
    }

    @Override
    public List<BrandVo> getCategoryBrand(Long catId) {
        LambdaQueryWrapper<CategoryBrandRelationEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CategoryBrandRelationEntity::getCatelogId, catId);
        return this.list(queryWrapper).stream().map(it -> {
            BrandVo brandVo = new BrandVo();
            BeanUtils.copyProperties(it, brandVo);
            return brandVo;
        }).toList();
    }


}
