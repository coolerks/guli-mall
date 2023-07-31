package top.integer.gulimall.order.service.impl;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import top.integer.gulimall.order.entity.OrderEntity;
import top.integer.gulimall.order.entity.OrderReturnReasonEntity;

import java.io.IOException;

@Component
@RabbitListener(queues = "boot.amqp.admin.q.001")
public class RabbitReceive {

    @RabbitHandler
    public void receiveOrderEntity(Message message, OrderEntity orderEntity, Channel channel) throws IOException {
        System.out.println("接收到了订单实体，orderEntity = " + orderEntity);
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        channel.basicAck(deliveryTag, false);

    }

    @RabbitHandler
    public void receiveOrderReturnReasonEntity(Message message, OrderReturnReasonEntity orderReturnReasonEntity, Channel channel) throws IOException {
        System.out.println("接收到了订单原因返回实体，orderReturnReasonEntity = " + orderReturnReasonEntity);
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
//        channel.basicReject();
//        channel.basicReject(deliveryTag, true);
        channel.basicAck(deliveryTag, false);

    }
}
