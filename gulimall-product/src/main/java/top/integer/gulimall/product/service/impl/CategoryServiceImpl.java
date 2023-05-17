package top.integer.gulimall.product.service.impl;

import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import top.integer.common.utils.PageUtils;
import top.integer.common.utils.Query;

import top.integer.gulimall.product.dao.CategoryDao;
import top.integer.gulimall.product.entity.CategoryEntity;
import top.integer.gulimall.product.service.CategoryService;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

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

    public CategoryEntity buildCategoryTree(List<CategoryEntity> all, CategoryEntity now) {
        List<CategoryEntity> children = all
                .stream()
                .filter(it -> it.getParentCid().equals(now.getCatId()))
                .map(it -> buildCategoryTree(all, it))
                .peek(it -> it.setSort(it.getSort() == null ? 0 : it.getSort()))
                .sorted(Comparator.comparingInt(CategoryEntity::getSort))
                .toList();
        now.setChildren(children);
        return now;
    }
}
