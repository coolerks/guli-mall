package top.integer.gulimall.product.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class MyThreadPoolConfig {
    @Bean
    public ThreadPoolExecutor threadPoolExecutor() {
        return new ThreadPoolExecutor(50, 100, 1, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(20000), Executors.defaultThreadFactory(), new ThreadPoolExecutor.DiscardPolicy());
    }
}
