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

import top.integer.gulimall.ware.dao.WareInfoDao;
import top.integer.gulimall.ware.entity.WareInfoEntity;
import top.integer.gulimall.ware.service.WareInfoService;


@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        LambdaQueryWrapper<WareInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        String key = (String) params.get("key");
        queryWrapper.or(StringUtils.isNotBlank(key), c -> c.like(WareInfoEntity::getName, "%" + key + "%")
                .or().like(WareInfoEntity::getAddress, "%" + key + "%")
                .or().eq(WareInfoEntity::getId, key)
                .or().eq(WareInfoEntity::getAreacode, key)
        );
        IPage<WareInfoEntity> page = this.page(new Query<WareInfoEntity>().getPage(params), queryWrapper);

        return new PageUtils(page);
    }

}
