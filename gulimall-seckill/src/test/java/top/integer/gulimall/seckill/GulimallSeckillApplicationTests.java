package top.integer.gulimall.seckill;

import org.junit.jupiter.api.Test;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import top.integer.gulimall.seckill.feign.SeckillFeign;
import top.integer.gulimall.seckill.schedule.SeckillSchedule;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
class GulimallSeckillApplicationTests {
	@Autowired
	SeckillFeign feign;

	@Autowired
	SeckillSchedule SeckillSchedule;

	@Autowired
	RedissonClient client;

	@Test
	void contextLoads() {
		SeckillSchedule.uploadSeckillSku();
	}

	@Test
	void add() {
		RSemaphore semaphore = client.getSemaphore("songxiaoxu");
		semaphore.trySetPermits(100);
	}

	@Test
	void subtract() {
		ExecutorService executorService = Executors.newCachedThreadPool();
		for (int i = 0; i < 500; i++) {
			executorService.execute(() -> {
				RSemaphore semaphore = client.getSemaphore("songxiaoxu");
				semaphore.tryAcquire(1);
			});
		}

	}

}
