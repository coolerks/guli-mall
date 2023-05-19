package top.integer.gulimall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import top.integer.gulimall.product.entity.CategoryEntity;
import top.integer.gulimall.product.service.CategoryService;
import top.integer.common.utils.PageUtils;
import top.integer.common.utils.R;


/**
 * 商品三级分类
 *
 * @author songxiaoxu
 * @email songxiaoxu2002@gmail.com
 * @date 2023-05-15 14:57:34
 */
@RestController
@RequestMapping("product/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 列表
     */
    @RequestMapping("/list/tree")
    public R list() {
        List<CategoryEntity> list = categoryService.listWithTree();
        return R.ok().put("data", list);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{catId}")
    public R info(@PathVariable("catId") Long catId) {
        CategoryEntity category = categoryService.getById(catId);

        return R.ok().put("category", category);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody CategoryEntity category) {
        categoryService.save(category);

        return R.ok();
    }

    @RequestMapping("/test2")
    public R test() {
        return R.ok().put("data", "成功00000");
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody CategoryEntity category) {
        categoryService.updateCascade(category);

        return R.ok();
    }

    @RequestMapping("/update/sort")
    public R updateSort(@RequestBody List<CategoryEntity> list) {
        categoryService.updateBatchById(list);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] catIds) {
        categoryService.removeMenuByIds(Arrays.asList(catIds));
        categoryService.removeByIds(Arrays.asList(catIds));

        return R.ok();
    }

}
