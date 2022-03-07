package org.board.springboot.auth.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
@Getter
public class AuthSession {
    private final Map<String, Object> session;
}
