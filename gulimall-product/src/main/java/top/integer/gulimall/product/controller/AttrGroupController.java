package top.integer.gulimall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import top.integer.gulimall.product.entity.AttrAttrgroupRelationEntity;
import top.integer.gulimall.product.entity.AttrEntity;
import top.integer.gulimall.product.entity.AttrGroupEntity;
import top.integer.gulimall.product.service.AttrAttrgroupRelationService;
import top.integer.gulimall.product.service.AttrGroupService;
import top.integer.common.utils.PageUtils;
import top.integer.common.utils.R;
import top.integer.gulimall.product.service.AttrService;
import top.integer.gulimall.product.vo.AttrGroupRelationVo;
import top.integer.gulimall.product.vo.AttrGroupWithAttrsVo;


/**
 * 属性分组
 *
 * @author songxiaoxu
 * @email songxiaoxu2002@gmail.com
 * @date 2023-05-15 14:57:34
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private AttrService attrService;

    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;


    /**
     * 列表
     */
    @RequestMapping("/list/{catalogId}")
    public R list(@RequestParam Map<String, Object> params, @PathVariable Long catalogId){
        PageUtils page = attrGroupService.queryPage(params, catalogId);

        return R.ok().put("page", page);
    }

    @GetMapping("/{catelogId}/withattr")
    public R getAttrGroupWithAttrs(@PathVariable Long catelogId) {
        List<AttrGroupWithAttrsVo> list = attrGroupService.getAttrGroupWithAttrsByCatelogId(catelogId);
        return R.ok().put("data", list);
    }

    @PostMapping("/attr/relation")
    public R addRelation(@RequestBody List<AttrGroupRelationVo> list) {
        if (list != null && !list.isEmpty()) {
            attrAttrgroupRelationService.saveBatch(list.stream().map(it -> {
                AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
                BeanUtils.copyProperties(it, attrAttrgroupRelationEntity);
                System.out.println("attrAttrgroupRelationEntity = " + attrAttrgroupRelationEntity);
                return attrAttrgroupRelationEntity;
            }).toList());
        }
        return R.ok();
    }

    @PostMapping("/attr/relation/delete")
    public R deleteRelation(@RequestBody List<AttrGroupRelationVo> list) {
        attrService.deleteRelation(list);
        return R.ok();
    }

    @GetMapping("/{attrgroupId}/attr/relation")
    public R attrRelation(@PathVariable String attrgroupId) {
        List<AttrEntity> list = attrService.getAttrRelation(attrgroupId);
        return R.ok().put("data", list);
    }

    @GetMapping("/{attrgroupId}/noattr/relation")
    public R noAttrReleation(@PathVariable Long attrgroupId, @RequestParam Map<String, Object> params) {
        PageUtils page = attrService.getNoAttrReleation(params, attrgroupId);
        return R.ok().put("page", page);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){
		AttrGroupEntity attrGroup = attrGroupService.getInfoById(attrGroupId);
        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

}
