package top.integer.gulimall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import top.integer.common.utils.PageUtils;
import top.integer.common.utils.Query;

import top.integer.gulimall.ware.constant.WareConstant;
import top.integer.gulimall.ware.dao.PurchaseDao;
import top.integer.gulimall.ware.entity.PurchaseDetailEntity;
import top.integer.gulimall.ware.entity.PurchaseEntity;
import top.integer.gulimall.ware.entity.WareSkuEntity;
import top.integer.gulimall.ware.service.PurchaseDetailService;
import top.integer.gulimall.ware.service.PurchaseService;
import top.integer.gulimall.ware.service.WareSkuService;
import top.integer.gulimall.ware.vo.MergeVo;
import top.integer.gulimall.ware.vo.PurchaseDoneVo;


@Service("purchaseService")
@Transactional
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Autowired
    private PurchaseDetailService purchaseDetailService;

    @Autowired
    private WareSkuService wareSkuService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageUnreceive(Map<String, Object> params) {
        LambdaQueryWrapper<PurchaseEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PurchaseEntity::getStatus, WareConstant.PurchaseStatusEnum.CREATED.getCode())
                .or()
                .eq(PurchaseEntity::getStatus, WareConstant.PurchaseStatusEnum.ASSIGNED.getCode());
        IPage<PurchaseEntity> page = this.page(new Query<PurchaseEntity>().getPage(params), queryWrapper);

        return new PageUtils(page);
    }

    @Override
    public void merge(MergeVo mergeVo) {
        Long purchaseId = mergeVo.getPurchaseId();
        if (purchaseId == null) {
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setUpdateTime(new Date());
            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.CREATED.getCode());
            this.save(purchaseEntity);
            purchaseId = purchaseEntity.getId();
        }
        PurchaseEntity purchaseEntity = this.getById(purchaseId);
        if (purchaseEntity == null) {
            return;
        }
        if (purchaseEntity.getStatus() != WareConstant.PurchaseStatusEnum.CREATED.getCode() && purchaseEntity.getStatus() != WareConstant.PurchaseStatusEnum.ASSIGNED.getCode()) {
            return;
        }
        final Long finalPurchaseId = purchaseId;
        List<PurchaseDetailEntity> list = mergeVo.getItems().stream().map(it -> {
            PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
            purchaseDetailEntity.setPurchaseId(finalPurchaseId);
            purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode());
            purchaseDetailEntity.setId(it);
            return purchaseDetailEntity;
        }).toList();

        LambdaUpdateWrapper<PurchaseEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(PurchaseEntity::getUpdateTime, new Date());
        updateWrapper.eq(PurchaseEntity::getId, purchaseId);

        this.update(updateWrapper);
        purchaseDetailService.updateBatchById(list);
    }

    @Override
    public void received(List<Long> ids) {
        List<PurchaseEntity> purchaseEntities = this.listByIds(ids.stream().distinct().toList());
        List<PurchaseEntity> list = purchaseEntities.stream()
                .filter(it -> it.getStatus() == WareConstant.PurchaseStatusEnum.CREATED.getCode() ||
                        it.getStatus() == WareConstant.PurchaseStatusEnum.ASSIGNED.getCode())
                .map(it -> {
                    PurchaseEntity purchaseEntity = new PurchaseEntity();
                    purchaseEntity.setId(it.getId());
                    purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.RECEIVE.getCode());
                    purchaseEntity.setUpdateTime(new Date());
                    return purchaseEntity;
                }).toList();
        if (!list.isEmpty()) {
            this.updateBatchById(list);

            LambdaUpdateWrapper<PurchaseDetailEntity> queryWrapper = new LambdaUpdateWrapper<>();
            queryWrapper.in(PurchaseDetailEntity::getPurchaseId, list.stream().map(PurchaseEntity::getId).toList());
            List<PurchaseDetailEntity> purchaseDetailEntities = purchaseDetailService.list(queryWrapper);

            List<Long> purchaseDetailIds = purchaseDetailEntities.stream().map(PurchaseDetailEntity::getId).toList();
            if (!purchaseDetailIds.isEmpty()) {
                LambdaUpdateWrapper<PurchaseDetailEntity> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.in(PurchaseDetailEntity::getId, purchaseDetailIds)
                        .set(PurchaseDetailEntity::getStatus, WareConstant.PurchaseDetailStatusEnum.BUYING.getCode());
                purchaseDetailService.update(updateWrapper);
            }
        }
    }

    @Override
    public void done(PurchaseDoneVo purchaseDoneVo) {

        // 改变采购单中每一项的状态
        boolean isAllFinish = purchaseDoneVo
                .getItems()
                .stream()
                .allMatch(it -> it.getStatus() == WareConstant.PurchaseDetailStatusEnum.FINISH.getCode());

        List<PurchaseDetailEntity> updateStatusPurchaseDetailEntities = purchaseDoneVo.getItems()
                .stream()
                .filter(it -> it.getStatus() != WareConstant.PurchaseDetailStatusEnum.HAS_ERROR.getCode())
                .peek(it -> {
                    // 将采购成功的库存更新
                    PurchaseDetailEntity purchaseDetail = this.purchaseDetailService.getById(it.getItemId());
                    this.wareSkuService.addStock(purchaseDetail.getSkuId(), purchaseDetail.getWareId(), purchaseDetail.getSkuNum());
                })
                .map(it -> {
                    PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
                    purchaseDetailEntity.setId(it.getItemId());
                    purchaseDetailEntity.setStatus(it.getStatus());
                    return purchaseDetailEntity;
                })
                .toList();
        this.purchaseDetailService.updateBatchById(updateStatusPurchaseDetailEntities);

        // 修改采购单的状态
        Long purchaseId = purchaseDoneVo.getId();

        PurchaseEntity purchase = new PurchaseEntity();
        purchase.setId(purchaseId);
        purchase.setUpdateTime(new Date());
        purchase.setStatus(isAllFinish ? WareConstant.PurchaseStatusEnum.FINISH.getCode() : WareConstant.PurchaseStatusEnum.HAS_ERROR.getCode());
        this.updateById(purchase);

    }

}
