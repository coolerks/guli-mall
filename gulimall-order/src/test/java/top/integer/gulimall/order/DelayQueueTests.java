package top.integer.gulimall.order;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
public class DelayQueueTests {
    @Autowired
    RabbitTemplate template;

    @Autowired
    AmqpAdmin amqpAdmin;

    @Test
    void createDelayQueue() {
        Queue queue = QueueBuilder
                .durable("test.delay.queue")
                .ttl(10000)
                .deadLetterExchange("test.normal.exchange")
                .deadLetterRoutingKey("key.normal.key")
                .build();
        amqpAdmin.declareQueue(queue);

        Exchange exchange = ExchangeBuilder
                .directExchange("test.delay.exchange")
                .build();
        amqpAdmin.declareExchange(exchange);

        amqpAdmin.declareBinding(BindingBuilder
                .bind(queue)
                .to(exchange)
                .with("key.delay.key")
                .noargs());

    }

    @Test
    void createNormalQueue() {
        Exchange exchange = ExchangeBuilder
                .directExchange("test.normal.exchange")
                .build();
        amqpAdmin.declareExchange(exchange);

        Queue queue = QueueBuilder
                .durable("test.normal.queue")
                .build();
        amqpAdmin.declareQueue(queue);

        amqpAdmin.declareBinding(BindingBuilder
                .bind(queue)
                .to(exchange)
                .with("key.normal.key")
                .noargs());

        createDelayQueue();
    }


    @Test
    void sendMessage() {
        for (int i = 0; i < 10; i++) {
            String message = "现在的时间（" + new Date() + "）";
            template.convertAndSend("test.normal.exchange", "key.normal.key", message);
        }

    }

    @Test
    void sendToDelayQueue() {
        for (int i = 0; i < 10; i++) {
            String message = "延迟消息，现在的时间（" + new Date() + "）";
            template.convertAndSend("order-event-exchange", "order.create.queue", message);
        }
    }

}
