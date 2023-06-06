package top.integer.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.integer.common.utils.PageUtils;
import top.integer.gulimall.product.entity.CategoryEntity;
import top.integer.gulimall.product.vo.CataLog1Vo;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author songxiaoxu
 * @email songxiaoxu2002@gmail.com
 * @date 2023-05-15 14:57:34
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<CategoryEntity> listWithTree();

    void removeMenuByIds(List<Long> asList);

    List<Long> getCatelogPath(Long catelogId);

    void updateCascade(CategoryEntity category);

    List<CategoryEntity> getLevel1Categories();

    Map<String, List<CataLog1Vo>> getCatelogJson();
}

