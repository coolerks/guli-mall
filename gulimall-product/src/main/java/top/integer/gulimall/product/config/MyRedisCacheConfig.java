package top.integer.gulimall.product.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

@Configuration
public class MyRedisCacheConfig {
    @Bean
    public RedisCacheConfiguration redisCacheConfiguration() {

        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(600L))
                .serializeValuesWith(RedisSerializationContext.
                        SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .prefixCacheNameWith("CACHE_");
    }
}
