package org.board.springboot.auth.service;

import lombok.RequiredArgsConstructor;
import org.board.springboot.auth.dto.LoginRequestDto;
import org.board.springboot.auth.dto.LoginUserResponseDto;
import org.board.springboot.user.dto.UserFindResponseDto;
import org.board.springboot.user.service.UserService;
import org.board.springboot.redis.user.UserSessionService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final UserService userService;
    private final UserSessionService userSessionService;

    public LoginUserResponseDto login(LoginRequestDto loginRequestDto, HttpSession httpSession) {
        validateLoginState(httpSession);

        UserFindResponseDto userFindResponseDto = userService.findByEmailAndPassword(loginRequestDto.toUserFindRequestDto());

        userSessionService.validateLoginEmailState(loginRequestDto.getEmail());
        userSessionService.createLoginState(loginRequestDto.getEmail());

        httpSession.setAttribute("login", loginRequestDto.getEmail());
        return LoginUserResponseDto.builder()
                .name(userFindResponseDto.getName())
                .email(userFindResponseDto.getEmail())
                .build();
    }

    private void validateLoginState(HttpSession httpSession) {
        if (httpSession.getAttribute("login") != null) {
            throw new IllegalArgumentException("이미 로그인 상태입니다.");
        }
    }

    public boolean logout(HttpSession httpSession) {
        validateLogoutState(httpSession);

        userSessionService.deleteLoginState(httpSession.getAttribute("login").toString());
        httpSession.removeAttribute("login");
        return true;
    }

    private void validateLogoutState(HttpSession httpSession) {
        if (httpSession.getAttribute("login") == null) {
            throw new IllegalArgumentException("로그인 상태가 아닙니다.");
        }
    }

    public boolean isLoggedIn(HttpSession httpSession) {
        return httpSession.getAttribute("login") != null;
    }
}
