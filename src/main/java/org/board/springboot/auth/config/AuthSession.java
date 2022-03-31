package org.board.springboot.auth.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;

@Getter
public class AuthSession {
    private final HashOperations<String, String, Object> session;

    @Autowired
    public AuthSession(RedisTemplate redisTemplate) {
        this.session = redisTemplate.opsForHash();
    }
}
