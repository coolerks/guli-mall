package top.integer.gulimall.ware;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import top.integer.common.to.LockStockTo;
import top.integer.common.utils.R;
import top.integer.gulimall.ware.dao.WareSkuDao;
import top.integer.gulimall.ware.feign.OrderFeign;
import top.integer.gulimall.ware.service.WareSkuService;
import top.integer.gulimall.ware.vo.OrderVo;
import top.integer.gulimall.ware.vo.StockVo;

import java.util.Date;
import java.util.List;
import java.util.Map;

@SpringBootTest
class GulimallWareApplicationTests {
    @Autowired
    WareSkuService wareSkuService;
    @Autowired
    WareSkuDao dao;
    @Autowired
    RabbitTemplate template;

    @Test
    void contextLoads() {
        Map<Long, Boolean> map = wareSkuService.hasStock(List.of(6L, 45L));
        System.out.println("map = " + map);
    }

    @Test
    void test() {
//        List<StockVo> stockVos = dao.listStock(List.of(6L, 9L, 45L, 58L));
////        System.out.println("stockVos = " + stockVos);
//        boolean b = wareSkuService.orderLockStock(List.of(
//                new LockStockTo(6L, 19),
//                new LockStockTo(9L, 10),
//                new LockStockTo(45L, 10),
//                new LockStockTo(58L, 10)
//        ));
//        System.out.println("b = " + b);
    }

    @Test
    void send() {
        for (int i = 0; i < 10; i++) {
            template.convertAndSend("stock-event-exchange", "stock.lock", "发送时间 " + new Date());
        }
    }

    @Autowired
    OrderFeign orderFeign;

    @Test
    void orderTest() {
        R abc = orderFeign.getOrderInfo("abc");
        System.out.println("abc = " + abc);
        OrderVo data = abc.getData(new TypeReference<OrderVo>() {
        });
        System.out.println("data = " + data);
        System.out.println("--------------------------");


        R abcd = orderFeign.getOrderInfo("abcd");
        System.out.println("abcd = " + abcd);
        OrderVo dataa = abcd.getData(new TypeReference<OrderVo>() {
        });
        System.out.println("data = " + dataa);
    }

}
