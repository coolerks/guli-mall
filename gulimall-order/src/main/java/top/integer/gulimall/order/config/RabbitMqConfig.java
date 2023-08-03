package top.integer.gulimall.order.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class RabbitMqConfig {

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Exchange orderEventExchange() {
        return ExchangeBuilder.topicExchange("order-event-exchange")
                .build();
    }

    @Bean
    public Queue orderReleaseQueue() {
        return QueueBuilder.durable("order.release.queue")
                .build();
    }

    @Bean
    public Queue orderDelayQueue() {
        return QueueBuilder.durable("order.delay.queue")
                .ttl(60000)
                .deadLetterExchange("order-event-exchange")
                .deadLetterRoutingKey("order.release.queue")
                .build();
    }

    @Bean
    public Binding releaseBinding(@Qualifier("orderEventExchange") Exchange exchange,
                                  @Qualifier("orderReleaseQueue") Queue queue) {
        return BindingBuilder.bind(queue)
                .to(exchange)
                .with("order.release.queue")
                .noargs();
    }

    @Bean
    public Binding delayBinding(@Qualifier("orderEventExchange") Exchange exchange,
                                  @Qualifier("orderDelayQueue") Queue queue) {
        return BindingBuilder.bind(queue)
                .to(exchange)
                .with("order.create.queue")
                .noargs();
    }


    @Bean
    public Binding orderReleaseOther() {
        return new Binding("stock.release.stock.queue", Binding.DestinationType.QUEUE,
                "order-event-exchange", "order.release.other.#", null);
    }

}
