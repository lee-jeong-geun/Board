package org.board.springboot.auth.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
@RequiredArgsConstructor
public class AuthConfig {

    private final RedisTemplate redisTemplate;

    @Bean
    public AuthSession authSession() {
        return new AuthSession(redisTemplate);
    }
}
