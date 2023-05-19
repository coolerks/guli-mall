package top.integer.gulimall.product.service.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Stream;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import top.integer.common.utils.PageUtils;
import top.integer.common.utils.Query;

import top.integer.gulimall.product.dao.CategoryDao;
import top.integer.gulimall.product.entity.CategoryEntity;
import top.integer.gulimall.product.service.CategoryBrandRelationService;
import top.integer.gulimall.product.service.CategoryService;


@Service("categoryService")
@Transactional
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        List<CategoryEntity> categoryEntities = baseMapper.selectList(null);
        return categoryEntities.stream()
                .filter(it -> it.getParentCid() == 0)
                .map(it -> buildCategoryTree(categoryEntities, it))
                .peek(it -> it.setSort(it.getSort() == null ? 0 : it.getSort()))
                .sorted(Comparator.comparingInt(CategoryEntity::getSort))
                .toList();
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
        // todo 检查要删除的id是否被引用
    }

    @Override
    public List<Long> getCatelogPath(Long catelogId) {
        CategoryEntity patentCategory = this.getById(catelogId);
        List<Long> parent = new ArrayList<>(3);
        parent.add(patentCategory.getCatId());
        if (patentCategory != null) {
            while (patentCategory.getParentCid() != 0 && (patentCategory = this.getById(patentCategory.getParentCid())) != null) {
                parent.add(patentCategory.getCatId());
            }
            Collections.reverse(parent);
        }
        return parent;
    }

    @Override
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        if (StringUtils.isNotEmpty(category.getName())) {
            categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
        }
    }


    public CategoryEntity buildCategoryTree(List<CategoryEntity> all, CategoryEntity parent) {
        List<CategoryEntity> children = all
                .stream()
                .filter(it -> it.getParentCid().equals(parent.getCatId()))
                .map(it -> buildCategoryTree(all, it))
                .peek(it -> it.setSort(it.getSort() == null ? 0 : it.getSort()))
                .sorted(Comparator.comparingInt(CategoryEntity::getSort))
                .toList();
        parent.setChildren(children);
        return parent;
    }
}
