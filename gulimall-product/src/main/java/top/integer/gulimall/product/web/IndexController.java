package top.integer.gulimall.product.web;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import top.integer.gulimall.product.entity.CategoryEntity;
import top.integer.gulimall.product.service.CategoryService;
import top.integer.gulimall.product.vo.CataLog1Vo;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller
@Slf4j
public class IndexController {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedissonClient redissonClient;

    @RequestMapping({"/", "/index.html"})
    public String index(Model model) {
        List<CategoryEntity> categories = categoryService.getLevel1Categories();
        model.addAttribute("categories", categories);
        return "index";
    }

    @ResponseBody
    @GetMapping("/index/catalog.json")
    public Map<String, List<CataLog1Vo>> getCatelogJson() {
        return categoryService.getCatelogJson();
    }

    @ResponseBody
    @GetMapping("/index/test")
    public String test() {
        return "ceshi";
    }

    @ResponseBody
    @GetMapping("/index/lock")
    public String lock() throws InterruptedException {
        log.info("尝试获得锁");
        final long start = System.currentTimeMillis();
        RLock lock = redissonClient.getFairLock("my-lock");
        final long lockedTime = System.currentTimeMillis();
        log.info("获得锁的时间：{}", lockedTime - start);
        boolean b = lock.tryLock(2, TimeUnit.SECONDS);
        if (b) {
            log.info("获取锁成功");
        } else {
            log.info("获取锁失败");
            return "获取锁失败";
        }
        log.info("上锁的时间：{}", System.currentTimeMillis() - lockedTime);
        Thread.sleep(20000L);
        log.info("解锁");
        lock.unlock();
        return "000";
    }

    @ResponseBody
    @GetMapping("/index/write")
    public String write() throws InterruptedException {
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock("my-lock");
        RLock rLock = readWriteLock.writeLock();
        rLock.lock();
        log.info("开始写数据，现在时间为：{}", new Date());
        Thread.sleep(3000L);
        rLock.unlock();
        return "";
    }

    @ResponseBody
    @GetMapping("/index/read")
    public String read() throws InterruptedException {
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock("my-lock");
        RLock rLock = readWriteLock.readLock();
        rLock.lock();
        log.info("开始读取数据，现在时间为：{}", new Date());
        Thread.sleep(3000L);
        rLock.unlock();
        return "";
    }

    @GetMapping("/index/await")
    @ResponseBody
    public String await() throws InterruptedException {
        RCountDownLatch countDownLatch = redissonClient.getCountDownLatch("my-lock");
        countDownLatch.trySetCount(10);
        countDownLatch.await();
        return "结束";
    }

    @GetMapping("/index/down")
    @ResponseBody
    public String down() {
        RCountDownLatch countDownLatch = redissonClient.getCountDownLatch("my-lock");
        countDownLatch.countDown();
        return "减少了";
    }

    @GetMapping("/index/semaphore")
    @ResponseBody
    public String semaphore() throws InterruptedException {
        RSemaphore semaphore = redissonClient.getSemaphore("my-lock");
        semaphore.acquire();
        log.info("获取到了一个信号量");
        Thread.sleep(10000L);
        semaphore.release();
        return "success";
    }

    @GetMapping("/index/spike")
    @ResponseBody
    public String spike() throws InterruptedException {
        RSemaphore semaphore = redissonClient.getSemaphore("my-lock");
        boolean b = semaphore.tryAcquire();
        if (b) {
            return "秒杀成功";
        }
        return "秒杀失败";
    }


}
