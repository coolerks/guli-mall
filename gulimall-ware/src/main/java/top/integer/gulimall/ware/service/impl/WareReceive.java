package top.integer.gulimall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.integer.common.to.mq.OrderCloseTo;
import top.integer.common.to.mq.StockLockedTo;
import top.integer.common.utils.R;
import top.integer.gulimall.ware.entity.WareOrderTaskDetailEntity;
import top.integer.gulimall.ware.entity.WareOrderTaskEntity;
import top.integer.gulimall.ware.entity.WareSkuEntity;
import top.integer.gulimall.ware.feign.OrderFeign;
import top.integer.gulimall.ware.service.WareOrderTaskDetailService;
import top.integer.gulimall.ware.service.WareOrderTaskService;
import top.integer.gulimall.ware.service.WareSkuService;
import top.integer.gulimall.ware.vo.OrderVo;

import java.io.IOException;
import java.util.List;

@RabbitListener(queues = "stock.release.stock.queue")
@Component
@Slf4j
public class WareReceive {
    @Autowired
    private WareOrderTaskService wareOrderTaskService;
    @Autowired
    private WareOrderTaskDetailService wareOrderTaskDetailService;
    @Autowired
    private OrderFeign orderFeign;
    @Autowired
    private WareSkuService wareSkuService;

    @RabbitHandler
    public void receive(Message message, String result, Channel channel) throws IOException {
        log.info("收到消息了，消息内容为：{}", result);
        long tag = message.getMessageProperties().getDeliveryTag();
        channel.basicAck(tag, false);
    }

    @RabbitHandler
    public void receiveStockLocked(Message message, StockLockedTo stockLockedTo, Channel channel) throws IOException {
        long tag = message.getMessageProperties().getDeliveryTag();
        log.info("收到消息了，消息内容为：{}", stockLockedTo);

        try {
            WareOrderTaskEntity orderTask = wareOrderTaskService.getById(stockLockedTo.getId());
            unlockStock(orderTask);
        } catch (Exception e) {
            channel.basicNack(tag, false, true);
            return;
        }

        channel.basicAck(tag, false);
    }

    @RabbitHandler
    public void receiveOrderClose(Message message, OrderCloseTo orderCloseTo, Channel channel) throws IOException {
        long tag = message.getMessageProperties().getDeliveryTag();
        log.info("收到关闭订单消息了，消息内容为：{}", orderFeign);

        try {
            WareOrderTaskEntity orderTask = wareOrderTaskService.getOne(new LambdaQueryWrapper<WareOrderTaskEntity>()
                    .eq(WareOrderTaskEntity::getOrderSn, orderCloseTo.getSn()));
            unlockStock(orderTask);
        } catch (Exception e) {
            channel.basicNack(tag, false, true);
            return;
        }

        channel.basicAck(tag, false);
    }

    private void unlockStock(WareOrderTaskEntity orderTask) {
        if (orderTask != null) {
            List<WareOrderTaskDetailEntity> list = wareOrderTaskDetailService.list(new LambdaQueryWrapper<WareOrderTaskDetailEntity>()
                    .eq(WareOrderTaskDetailEntity::getTaskId, orderTask.getId()));
            System.out.println("list = " + list);
            if (list != null && !list.isEmpty()) {
                R r = orderFeign.getOrderInfo(orderTask.getOrderSn());
                OrderVo order = r.getData(new TypeReference<OrderVo>() {
                });
                System.out.println("r = " + r);
                if (order == null || order.getStatus() == 4) {
                    for (WareOrderTaskDetailEntity orderTaskDetail : list) {
                        if (orderTaskDetail.getLockStatus() == 1) {
                            UpdateWrapper<WareSkuEntity> updateWrapper = new UpdateWrapper<>();
                            updateWrapper.setSql("stock = stock + " + orderTaskDetail.getSkuNum())
                                    .setSql("stock_locked = stock_locked - " + orderTaskDetail.getSkuNum())
                                    .eq("id", orderTaskDetail.getWareId());
                            wareSkuService.update(updateWrapper);
                            orderTaskDetail.setLockStatus(2);
                            this.wareOrderTaskDetailService.updateById(orderTaskDetail);
                        }

                    }
                }
            }
        }
    }
}
