package com.amigoscode;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.session.data.redis.RedisIndexedSessionRepository;

@Configuration
public class RedisConfig {

//    @Bean
    public SessionRepository<? extends Session> sessionRepository(
            RedisOperations<String, Object> operations) {
        return new RedisIndexedSessionRepository(operations);
    }
}
