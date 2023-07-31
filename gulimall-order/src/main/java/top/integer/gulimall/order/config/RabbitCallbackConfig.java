package top.integer.gulimall.order.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@Slf4j
public class RabbitCallbackConfig {
    @Autowired
    private RabbitTemplate template;

    @PostConstruct
    public void setConfirmCallback() {
        template.setConfirmCallback((correlationData, ack, cause) -> {
            log.info("数据：{}, ack: {}, cause: {}", correlationData, ack, cause);
        });

        template.setReturnsCallback(returned -> {
            log.info("没有队列收到此消息，返回的信息为：{}", returned);
        });

    }
}
