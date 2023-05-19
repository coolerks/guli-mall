package top.integer.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import top.integer.common.utils.PageUtils;
import top.integer.common.utils.Query;

import top.integer.gulimall.product.dao.BrandDao;
import top.integer.gulimall.product.entity.BrandEntity;
import top.integer.gulimall.product.service.BrandService;
import top.integer.gulimall.product.service.CategoryBrandRelationService;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        LambdaQueryWrapper<BrandEntity> queryWrapper = new LambdaQueryWrapper<>();
        String key = (String) params.get("key");
        queryWrapper.eq(StringUtils.isNotEmpty(key), BrandEntity::getBrandId, key)
                .or().like(StringUtils.isNotEmpty(key), BrandEntity::getName, key);
        IPage<BrandEntity> page = this.page(
                new Query<BrandEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void updateDetail(BrandEntity brand) {
        this.updateById(brand);
        if (StringUtils.isNotEmpty(brand.getName())) {
            categoryBrandRelationService.updateBrand(brand.getBrandId(), brand.getName());
        }
        // todo 更新其他
    }

}
