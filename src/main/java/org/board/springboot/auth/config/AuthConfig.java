package org.board.springboot.auth.config;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.Key;

@Configuration
public class AuthConfig {

    private final String secretKey = "qwertyu123iopasdfghjklzxcvbnmqwert456";

    @Bean
    public SignatureAlgorithm signatureAlgorithm() {
        return SignatureAlgorithm.HS256;
    }

    @Bean
    public Key key() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
}
