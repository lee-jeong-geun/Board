package org.board.springboot.auth.service;

import lombok.RequiredArgsConstructor;
import org.board.springboot.auth.config.AuthSession;
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
    private final AuthSession authSession;

    public LoginUserResponseDto login(LoginRequestDto loginRequestDto, HttpSession httpSession) {
        validateLoginState(httpSession);

        UserFindResponseDto userFindResponseDto = userService.find(loginRequestDto.toUserFindRequestDto());

        validateLoginEmailState(loginRequestDto);

        authSession.getSession().put(loginRequestDto.getEmail(), true);
        httpSession.setAttribute("login", loginRequestDto.getEmail());
        return LoginUserResponseDto.builder()
                .name(userFindResponseDto.getName())
                .email(userFindResponseDto.getEmail())
                .build();
    }

    private void validateLoginEmailState(LoginRequestDto loginRequestDto) {
        if (authSession.getSession().containsKey(loginRequestDto.getEmail())) {
            throw new IllegalArgumentException("해당 아이디는 다른곳에서 로그인 중입니다.");
        }
    }

    private void validateLoginState(HttpSession httpSession) {
        if (httpSession.getAttribute("login") != null) {
            throw new IllegalArgumentException("이미 로그인 상태입니다.");
        }
    }

    public boolean logout(HttpSession httpSession) {
        validateLogoutState(httpSession);

        authSession.getSession().remove(httpSession.getAttribute("login"));
        httpSession.removeAttribute("login");
        return true;
    }

    private void validateLogoutState(HttpSession httpSession) {
        if (httpSession.getAttribute("login") == null) {
            throw new IllegalArgumentException("로그인 상태가 아닙니다.");
        }
    }
}
