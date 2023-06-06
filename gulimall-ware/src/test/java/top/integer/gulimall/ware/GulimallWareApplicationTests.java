package top.integer.gulimall.ware;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import top.integer.gulimall.ware.service.WareSkuService;

import java.util.List;
import java.util.Map;

@SpringBootTest
class GulimallWareApplicationTests {
    @Autowired
    WareSkuService wareSkuService;
    @Test
    void contextLoads() {
        Map<Long, Boolean> map = wareSkuService.hasStock(List.of(6L, 45L));
        System.out.println("map = " + map);
    }

}
