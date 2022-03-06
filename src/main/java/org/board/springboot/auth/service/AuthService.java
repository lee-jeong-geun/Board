package org.board.springboot.auth.service;

import lombok.RequiredArgsConstructor;
import org.board.springboot.auth.dto.LoginRequestDto;
import org.board.springboot.auth.dto.LoginUserResponseDto;
import org.board.springboot.user.dto.UserFindResponseDto;
import org.board.springboot.user.service.UserService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final UserService userService;

    public LoginUserResponseDto login(LoginRequestDto loginRequestDto, HttpSession httpSession) {
        validateLoginState(httpSession);

        UserFindResponseDto userFindResponseDto = userService.find(loginRequestDto.toUserFindRequestDto());
        httpSession.setAttribute("login", true);
        return LoginUserResponseDto.builder()
                .name(userFindResponseDto.getName())
                .email(userFindResponseDto.getEmail())
                .build();
    }

    private void validateLoginState(HttpSession httpSession) {
        if (httpSession.getAttribute("login") != null && (boolean) httpSession.getAttribute("login")) {
            throw new IllegalArgumentException("이미 로그인 상태입니다.");
        }
    }


    public boolean logout(HttpSession httpSession) {
        validateLogoutState(httpSession);

        httpSession.setAttribute("login", false);
        return true;
    }

    private void validateLogoutState(HttpSession httpSession) {
        if (httpSession.getAttribute("login") == null || !(boolean) httpSession.getAttribute("login")) {
            throw new IllegalArgumentException("로그인 상태가 아닙니다.");
        }
    }
}
