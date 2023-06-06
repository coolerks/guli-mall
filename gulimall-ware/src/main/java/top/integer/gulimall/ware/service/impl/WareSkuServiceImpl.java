package top.integer.gulimall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import top.integer.common.utils.PageUtils;
import top.integer.common.utils.Query;

import top.integer.common.utils.R;
import top.integer.gulimall.ware.dao.WareSkuDao;
import top.integer.gulimall.ware.entity.WareSkuEntity;
import top.integer.gulimall.ware.feign.ProductFeign;
import top.integer.gulimall.ware.service.WareSkuService;


@Service("wareSkuService")
@Slf4j
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {
    @Autowired
    private ProductFeign productFeign;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String skuId = (String) params.get("skuId");
        String wareId = (String) params.get("wareId");
        LambdaQueryWrapper<WareSkuEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotBlank(skuId), WareSkuEntity::getSkuId, skuId)
                .eq(StringUtils.isNotBlank(wareId), WareSkuEntity::getWareId, wareId);
        IPage<WareSkuEntity> page = this.page(new Query<WareSkuEntity>().getPage(params), queryWrapper);

        return new PageUtils(page);
    }

    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        LambdaQueryWrapper<WareSkuEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WareSkuEntity::getWareId, wareId)
                .eq(WareSkuEntity::getSkuId, skuId);
        if (baseMapper.exists(queryWrapper)) {
            baseMapper.addStock(skuId, wareId, skuNum);
        } else {
            WareSkuEntity wareSku = new WareSkuEntity();
            try {
                R info = productFeign.info(skuId);
                if (Objects.equals(info.get("code"), 0)) {
                    Map<String, Object> skuInfo = (Map<String, Object>) info.get("skuInfo");
                    wareSku.setSkuName((String) skuInfo.get("skuName"));
                }
            } catch (Throwable e) {
                e.printStackTrace();
                log.error("远程调用获取sku的信息时出异常了...");
            }
            wareSku.setStock(skuNum);
            wareSku.setWareId(wareId);
            wareSku.setSkuId(skuId);
            baseMapper.insert(wareSku);
        }
    }

    @Override
    public Map<Long, Boolean> hasStock(List<Long> skuIds) {
        QueryWrapper<WareSkuEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("sku_id", " sum(stock) - sum(stock_locked) stock")
                .in("sku_id", skuIds)
                .groupBy("sku_id");
        List<WareSkuEntity> wareSkuEntities = baseMapper.selectList(queryWrapper);
        return wareSkuEntities.stream()
                .collect(Collectors.toMap(WareSkuEntity::getSkuId, w -> w.getStock() > 0));
    }

}
