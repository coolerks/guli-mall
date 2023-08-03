package top.integer.gulimall.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.integer.common.to.mq.OrderCloseTo;
import top.integer.gulimall.order.entity.OrderEntity;
import top.integer.gulimall.order.enums.OrderStatusEnum;
import top.integer.gulimall.order.service.OrderService;

import java.io.IOException;

@RabbitListener(queues = {"order.release.queue"})
@Component
@Slf4j
public class OrderReleaseQueueReceive {
    @Autowired
    private OrderService orderService;
    @Autowired
    private RabbitTemplate template;

    @RabbitHandler
    public void receive(Message message, OrderCloseTo orderCloseTo, Channel channel) throws IOException {
        long tag = message.getMessageProperties().getDeliveryTag();
        log.info("准备关单");
        try {
            OrderEntity order = orderService.getById(orderCloseTo.getId());
            System.out.println("order = " + order);
            if (order != null && OrderStatusEnum.CREATE_NEW.getCode().equals(order.getStatus())) {
                LambdaUpdateWrapper<OrderEntity> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.set(OrderEntity::getStatus, OrderStatusEnum.CANCLED.getCode());
                orderService.update(updateWrapper);
                template.convertAndSend("order-event-exchange", "order.release.other.queue", orderCloseTo);
            }
        } catch (Exception e) {
            channel.basicNack(tag, false, true);
        }
        channel.basicAck(tag, false);
    }
}
