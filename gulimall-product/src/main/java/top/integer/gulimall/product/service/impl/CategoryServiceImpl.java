package top.integer.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.integer.common.utils.PageUtils;
import top.integer.common.utils.Query;
import top.integer.gulimall.product.dao.CategoryDao;
import top.integer.gulimall.product.entity.CategoryEntity;
import top.integer.gulimall.product.service.CategoryBrandRelationService;
import top.integer.gulimall.product.service.CategoryService;
import top.integer.gulimall.product.vo.CataLog1Vo;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service("categoryService")
@Transactional
@Slf4j
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RedissonClient redissonClient;

    private final String script = """
            if redis.call("get",KEYS[1]) == ARGV[1]
            then
                return redis.call("del",KEYS[1])
            else
                return 0
            end
            """;

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

    @CacheEvict(value = "catalog", allEntries = true)
    @Override
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        if (StringUtils.isNotEmpty(category.getName())) {
            categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
        }
    }

    @Cacheable(value = "catalog", key = "#root.methodName", sync = true)
    @Override
    public List<CategoryEntity> getLevel1Categories() {
        return getCategoriesByLevel(1L);
    }

    @Cacheable(value = "catalog", key = "#root.methodName", sync = true)
    @Override
    public Map<String, List<CataLog1Vo>> getCatelogJson() {
        log.info("没有缓存");
        List<CategoryEntity> categoryEntities = this.listWithTree();
        Map<String, List<CataLog1Vo>> collect = categoryEntities.stream()
                .collect(Collectors.toMap(e -> e.getCatId().toString(), CategoryEntity::getChildren))
                .entrySet()
                .stream()
                .map(CategoryServiceImpl::getCatalog1Entry)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return collect;
    }

    /**
     * 生成二级分类
     * @param it 当前一级分类下的所有的二级分类
     * @return
     */
    private static Map.Entry<String, List<CataLog1Vo>> getCatalog1Entry(Map.Entry<String, List<CategoryEntity>> it) {
        String key = it.getKey();
        // 收集二级分类
        List<CataLog1Vo> cataLog1Vos = it.getValue().stream().map(getCategoryEntityCataLog1Vo(key)).toList();

        return Map.entry(key, cataLog1Vos);
    }

    /**
     * 根据传入的key，生成三级分类
     * @param key
     * @return
     */
    private static Function<CategoryEntity, CataLog1Vo> getCategoryEntityCataLog1Vo(String key) {
        return item -> {
            CataLog1Vo cataLog1Vo = new CataLog1Vo();

            cataLog1Vo.setId(item.getCatId().toString());
            cataLog1Vo.setName(item.getName());
            cataLog1Vo.setCatalog1Id(key);
            // 收集三级分类
            List<CataLog1Vo.CataLog3Vo> cataLog3Vos = item.getChildren()
                    .stream()
                    .map(c -> new CataLog1Vo.CataLog3Vo(cataLog1Vo.getId(), c.getCatId().toString(), c.getName()))
                    .toList();

            cataLog1Vo.setCatalog3List(cataLog3Vos);

            return cataLog1Vo;
        };
    }

    public List<CategoryEntity> getCategoriesByLevel(Long level) {
        LambdaQueryWrapper<CategoryEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CategoryEntity::getCatLevel, level);
        return this.list(queryWrapper);
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
