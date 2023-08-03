package top.integer.gulimall.ware.config;

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
    public Exchange stockEventExchange() {
        return ExchangeBuilder.topicExchange("stock-event-exchange")
                .build();
    }

    @Bean
    public Queue stockReleaseQueue() {
        return QueueBuilder.durable("stock.release.stock.queue")
                .build();
    }

    @Bean
    public Queue stockDelayQueue() {
        return QueueBuilder.durable("stock.delay.queue")
                .ttl(60000 * 2)
                .deadLetterExchange("stock-event-exchange")
                .deadLetterRoutingKey("stock.release.stock.queue")
                .build();
    }

    @Bean
    public Binding releaseBinding(@Qualifier("stockEventExchange") Exchange exchange,
                                  @Qualifier("stockReleaseQueue") Queue queue) {
        return BindingBuilder.bind(queue)
                .to(exchange)
                .with("stock.release.#")
                .noargs();
    }

    @Bean
    public Binding delayBinding(@Qualifier("stockEventExchange") Exchange exchange,
                                  @Qualifier("stockDelayQueue") Queue queue) {
        return BindingBuilder.bind(queue)
                .to(exchange)
                .with("stock.lock")
                .noargs();
    }

}
