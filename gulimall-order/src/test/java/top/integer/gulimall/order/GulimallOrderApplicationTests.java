package top.integer.gulimall.order;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import top.integer.common.utils.R;
import top.integer.common.vo.ProductInfoVo;
import top.integer.gulimall.order.entity.OrderEntity;
import top.integer.gulimall.order.entity.OrderReturnReasonEntity;
import top.integer.gulimall.order.feign.CartFeign;
import top.integer.gulimall.order.feign.MemberAddressFeign;
import top.integer.gulimall.order.feign.SkuInfoFeign;
import top.integer.gulimall.order.feign.WmsFeign;
import top.integer.gulimall.order.vo.MemberAddressVo;
import top.integer.gulimall.order.vo.OrderItemVo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@SpringBootTest
class GulimallOrderApplicationTests {
    @Autowired
    AmqpAdmin amqpAdmin;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MemberAddressFeign feign;

    @Autowired
    CartFeign cartFeign;

    @Autowired
    SkuInfoFeign skuInfoFeign;

    @Autowired
    WmsFeign wmsFeign;


    @Test
    void price() {
        Map<Long, BigDecimal> skusPrice = skuInfoFeign.getSkusPrice(List.of(6L, 15L));
        System.out.println("skusPrice = " + skusPrice);
    }

    @Test
    void spuInfo() {
        Map<Long, ProductInfoVo> spuInfoBySkuIds = skuInfoFeign.getSpuInfoBySkuIds(List.of(67L, 68L, 70L));
        System.out.println("spuInfoBySkuIds = " + spuInfoBySkuIds);
    }

    @Test
    void hasStock() {
        R r = wmsFeign.hasStock(List.of(9L, 45L));
        Map<Long, Boolean> data = r.getData(new TypeReference<Map<Long, Boolean>>() {
        });
        System.out.println("data = " + data);
    }

    @Test
    void address() {
        List<MemberAddressVo> memberAddress = feign.getMemberAddress(1L);
        System.out.println("memberAddress = " + memberAddress);
    }

    @Test
    void cart() {
        List<OrderItemVo> currentUserCartItems = cartFeign.getCurrentUserCartItems();
        System.out.println("currentUserCartItems = " + currentUserCartItems);
    }


    @Test
    void sendMessage() throws JsonProcessingException {
        OrderReturnReasonEntity order = new OrderReturnReasonEntity();
        order.setId(10L);
        order.setName("aaaaa222");
        order.setSort(10);
        order.setStatus(200);
        order.setCreateTime(new Date());
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
        Message message = new Message(objectMapper.writeValueAsBytes(order), messageProperties);

        OrderEntity orderEntity = new OrderEntity();

//        rabbitTemplate.send("boot.amqp.admin.001", "FIRST_KEY", message);
        for (int i = 0; i < 20; i++) {
            order.setId((long) i);
            orderEntity.setId((long) i);
            if (i % 2 == 0) {
                rabbitTemplate.convertAndSend("boot.amqp.admin.001", "FIRST_KEY", order);
            } else {
                rabbitTemplate.convertAndSend("boot.amqp.admin.001", "FIRST_KEY", orderEntity);
            }
        }

    }



    @Test
    void contextLoads() {
        amqpAdmin.declareExchange(ExchangeBuilder
                .directExchange("boot.amqp.admin.001")
                .durable(true)
                .build());
    }

    @Test
    void createQueue() {
        amqpAdmin.declareQueue(QueueBuilder
                .durable("boot.amqp.admin.q.001")
                .build());
    }

    @Test
    void bindExchangeAndQueue() {
        Exchange exchange = ExchangeBuilder
                .directExchange("boot.amqp.admin.001")
                .durable(true)
                .build();

        Queue queue = QueueBuilder
                .durable("boot.amqp.admin.q.001")
                .build();

        amqpAdmin.declareExchange(exchange);
        amqpAdmin.declareQueue(queue);

        amqpAdmin.declareBinding(BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(RoutingKey.FIRST_KEY)
                .noargs());
    }


    enum RoutingKey {
        FIRST_KEY,
        SECOND_KEY
    }

}
