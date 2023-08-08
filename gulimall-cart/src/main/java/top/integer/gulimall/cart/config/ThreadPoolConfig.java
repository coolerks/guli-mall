package top.integer.gulimall.cart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ThreadPoolConfig {
    @Bean(name = "my-thread-pool")
    public ThreadPoolExecutor threadPoolExecutor() {
        return new ThreadPoolExecutor(100, 200, 0, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(150), new ThreadPoolExecutor.DiscardPolicy());
    }
}
