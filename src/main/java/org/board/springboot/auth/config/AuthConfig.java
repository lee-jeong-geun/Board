package org.board.springboot.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class AuthConfig {

    @Bean
    public AuthSession authSession() {
        return new AuthSession(new ConcurrentHashMap<>());
    }
}
