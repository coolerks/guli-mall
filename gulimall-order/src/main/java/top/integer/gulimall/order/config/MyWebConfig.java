package top.integer.gulimall.order.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.integer.gulimall.order.interceptor.OrderInterceptor;

@Configuration
public class MyWebConfig implements WebMvcConfigurer {
    @Autowired
    private OrderInterceptor orderInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(orderInterceptor).addPathPatterns("/**");
    }
}
