package top.integer.gulimall.coupon;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import top.integer.gulimall.coupon.service.CouponService;
import top.integer.gulimall.coupon.service.SeckillSessionService;

import java.time.*;
import java.time.temporal.TemporalUnit;
import java.util.Date;

@SpringBootTest
class GulimallCouponApplicationTests {

	@Autowired
	SeckillSessionService service;
	@Test
	void contextLoads() {
		System.out.println("service.getLastest3DaysSku() = " + service.getLastest3DaysSku());
	}

}

class Main {
	public static void main(String[] args) {

	}
}
