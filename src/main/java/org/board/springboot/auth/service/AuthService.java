package org.board.springboot.auth.service;

import lombok.RequiredArgsConstructor;
import org.board.springboot.auth.dto.LoginRequestDto;
import org.board.springboot.auth.dto.LoginUserResponseDto;
import org.board.springboot.redis.user.UserSessionService;
import org.board.springboot.user.dto.UserFindResponseDto;
import org.board.springboot.user.service.UserService;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final UserService userService;
    private final UserSessionService userSessionService;
    private final JWTService jwtService;
    private final HttpServletRequest httpServletRequest;
    private final HttpServletResponse httpServletResponse;

    private static final int JWT_COOKIE_MAX_AGE = 60 * 30;

    public LoginUserResponseDto login(LoginRequestDto loginRequestDto) {
        validateLoginState();

        UserFindResponseDto userFindResponseDto = userService.findByEmailAndPassword(loginRequestDto.toUserFindRequestDto());

        userSessionService.validateLoginEmailState(loginRequestDto.getEmail());
        userSessionService.createLoginState(loginRequestDto.getEmail());

        Cookie cookie = new Cookie("token", jwtService.createJWT(loginRequestDto.getEmail()));
        cookie.setMaxAge(JWT_COOKIE_MAX_AGE);
        cookie.setPath("/");
        httpServletResponse.addCookie(cookie);
        return LoginUserResponseDto.builder()
                .name(userFindResponseDto.getName())
                .email(userFindResponseDto.getEmail())
                .build();
    }

    private void validateLoginState() {
        Cookie tokenCookie = getCookie(httpServletRequest.getCookies(), "token");
        if (tokenCookie != null && jwtService.validateJWT(tokenCookie.getValue())) {
            throw new IllegalArgumentException("이미 로그인 상태입니다.");
        }
    }

    public boolean logout() {
        Cookie tokenCookie = getCookie(httpServletRequest.getCookies(), "token");
        validateLogoutState(tokenCookie);

        userSessionService.deleteLoginState(jwtService.getEmail(tokenCookie.getValue()));

        tokenCookie.setPath("/");
        tokenCookie.setMaxAge(0);
        httpServletResponse.addCookie(tokenCookie);
        return true;
    }

    private void validateLogoutState(Cookie tokenCookie) {
        if (tokenCookie == null || !jwtService.validateJWT(tokenCookie.getValue())) {
            throw new IllegalArgumentException("로그인 상태가 아닙니다.");
        }
    }

    public boolean isLoggedIn() {
        Cookie tokenCookie = getCookie(httpServletRequest.getCookies(), "token");
        return tokenCookie != null && jwtService.validateJWT(tokenCookie.getValue());
    }

    private Cookie getCookie(Cookie[] cookies, String name) {
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)) {
                return cookie;
            }
        }
        return null;
    }
}
