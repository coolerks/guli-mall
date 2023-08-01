package top.integer.gulimall.ware;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import top.integer.common.to.LockStockTo;
import top.integer.gulimall.ware.dao.WareSkuDao;
import top.integer.gulimall.ware.service.WareSkuService;
import top.integer.gulimall.ware.vo.StockVo;

import java.util.List;
import java.util.Map;

@SpringBootTest
class GulimallWareApplicationTests {
    @Autowired
    WareSkuService wareSkuService;
    @Autowired
    WareSkuDao dao;

    @Test
    void contextLoads() {
        Map<Long, Boolean> map = wareSkuService.hasStock(List.of(6L, 45L));
        System.out.println("map = " + map);
    }

    @Test
    void test() {
//        List<StockVo> stockVos = dao.listStock(List.of(6L, 9L, 45L, 58L));
//        System.out.println("stockVos = " + stockVos);
        boolean b = wareSkuService.orderLockStock(List.of(
                new LockStockTo(6L, 19),
                new LockStockTo(9L, 10),
                new LockStockTo(45L, 10),
                new LockStockTo(58L, 10)
        ));
        System.out.println("b = " + b);
    }

}
