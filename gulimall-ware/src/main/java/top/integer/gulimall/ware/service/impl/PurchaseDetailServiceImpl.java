package top.integer.gulimall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import top.integer.common.utils.PageUtils;
import top.integer.common.utils.Query;

import top.integer.gulimall.ware.dao.PurchaseDetailDao;
import top.integer.gulimall.ware.entity.PurchaseDetailEntity;
import top.integer.gulimall.ware.service.PurchaseDetailService;


@Service("purchaseDetailService")
public class PurchaseDetailServiceImpl extends ServiceImpl<PurchaseDetailDao, PurchaseDetailEntity> implements PurchaseDetailService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        LambdaQueryWrapper<PurchaseDetailEntity> queryWrapper = new LambdaQueryWrapper<>();
        String status = (String) params.get("status");
        String wareId = (String) params.get("wareId");
        String key = (String) params.get("key");
        queryWrapper.eq(StringUtils.isNotBlank(status), PurchaseDetailEntity::getStatus, status)
                .eq(StringUtils.isNotBlank(wareId), PurchaseDetailEntity::getWareId, wareId)
                .and(StringUtils.isNotBlank(key), c -> c.eq(PurchaseDetailEntity::getPurchaseId, key)
                        .or().eq(PurchaseDetailEntity::getSkuId, key));
        IPage<PurchaseDetailEntity> page = this.page(new Query<PurchaseDetailEntity>().getPage(params), queryWrapper);

        return new PageUtils(page);
    }

}
