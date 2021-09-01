package com.nutritiondesigner.authservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class RedisConfig {

    /* Redis 와 Connection 을 생성해주는 객체 */
    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        // host 주소, 포트 번호
        return new LettuceConnectionFactory("localhost", 6379);
    }

    /* Redis 는 RedisTemplate 을 통해서 Redis 서버와 통신한다. */
    @Bean
    public RedisTemplate<?, ?> redisTemplate() {
        RedisTemplate<byte[], byte[]> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        return redisTemplate;
    }
}
