package top.integer.gulimall.product.controller;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import top.integer.common.vo.ProductInfoVo;
import top.integer.gulimall.product.entity.SkuInfoEntity;
import top.integer.gulimall.product.entity.SpuInfoEntity;
import top.integer.gulimall.product.service.SkuInfoService;
import top.integer.common.utils.PageUtils;
import top.integer.common.utils.R;



/**
 * sku信息
 *
 * @author songxiaoxu
 * @email songxiaoxu2002@gmail.com
 * @date 2023-05-15 14:57:34
 */
@RestController
@RequestMapping("product/skuinfo")
public class SkuInfoController {
    @Autowired
    private SkuInfoService skuInfoService;

    @PostMapping("/price")
    public Map<Long, BigDecimal> getPrice(@RequestBody List<Long> ids) {
        return skuInfoService.getPrice(ids);
    }

    @PostMapping("/skuId")
    public Map<Long, ProductInfoVo> getSpuInfoBySkuIds(@RequestBody List<Long> ids) {
        return skuInfoService.getSpuInfoBySkuIds(ids);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = skuInfoService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{skuId}")
    public R info(@PathVariable("skuId") Long skuId){
		SkuInfoEntity skuInfo = skuInfoService.getById(skuId);

        return R.ok().put("skuInfo", skuInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody SkuInfoEntity skuInfo){
		skuInfoService.save(skuInfo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody SkuInfoEntity skuInfo){
		skuInfoService.updateById(skuInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] skuIds){
		skuInfoService.removeByIds(Arrays.asList(skuIds));

        return R.ok();
    }

}
