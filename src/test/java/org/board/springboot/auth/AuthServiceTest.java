package org.board.springboot.auth;

import org.board.springboot.auth.dto.LoginRequestDto;
import org.board.springboot.auth.dto.LoginUserResponseDto;
import org.board.springboot.auth.service.AuthService;
import org.board.springboot.user.domain.User;
import org.board.springboot.user.dto.UserFindResponseDto;
import org.board.springboot.user.service.UserService;
import org.board.springboot.redis.user.UserSessionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpSession;

import java.util.Optional;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;

@RunWith(MockitoJUnitRunner.class)
public class AuthServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private UserSessionService userSessionService;

    @Mock
    private MockHttpSession mockHttpSession;

    @InjectMocks
    private AuthService authService;

    private final String email = "jk@jk.com";

    @Test
    public void login_userService_호출_성공() {
        //given
        String name = "jk";
        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                .email(email)
                .build();
        UserFindResponseDto userFindResponseDto = new UserFindResponseDto(User.builder()
                .name(name)
                .email(email)
                .build());
        given(mockHttpSession.getAttribute("login")).willReturn(null);
        given(userService.findByEmailAndPassword(any())).willReturn(userFindResponseDto);

        //when
        LoginUserResponseDto result = authService.login(loginRequestDto, mockHttpSession);

        //then
        BDDMockito.then(mockHttpSession).should().getAttribute("login");
        BDDMockito.then(userService).should().findByEmailAndPassword(any());
        BDDMockito.then(userSessionService).should().validateLoginEmailState(email);
        BDDMockito.then(userSessionService).should().createLoginState(email);
        BDDMockito.then(mockHttpSession).should().setAttribute("login", email);
        then(result.getName()).isEqualTo(name);
        then(result.getEmail()).isEqualTo(email);
    }

    @Test
    public void logout_호출_성공() {
        //given
        given(mockHttpSession.getAttribute("login")).willReturn(true);

        //when
        boolean result = authService.logout(mockHttpSession);

        //then
        BDDMockito.then(mockHttpSession).should(times(2)).getAttribute("login");
        BDDMockito.then(userSessionService).should().deleteLoginState(mockHttpSession.getAttribute("login").toString());
        BDDMockito.then(mockHttpSession).should().removeAttribute("login");
        then(result).isEqualTo(true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void logout_세션_null_값_호출_실패_에러() {
        //given
        given(mockHttpSession.getAttribute("login")).willReturn(null);

        //when
        authService.logout(mockHttpSession);
    }

    @Test(expected = IllegalArgumentException.class)
    public void login_조회_실패_에러() {
        //given
        LoginRequestDto loginRequestDto = LoginRequestDto.builder().build();
        given(userService.findByEmailAndPassword(any())).willThrow(new IllegalArgumentException("해당 유저가 없습니다."));

        //when
        authService.login(loginRequestDto, mockHttpSession);
    }

    @Test(expected = IllegalArgumentException.class)
    public void login_세션_not_null_값_호출_실패_에러() {
        //given
        LoginRequestDto loginRequestDto = LoginRequestDto.builder().build();
        given(mockHttpSession.getAttribute("login")).willReturn(Optional.empty());

        //when
        authService.login(loginRequestDto, mockHttpSession);
    }

    @Test(expected = IllegalArgumentException.class)
    public void login_이메일_로그인_상태_호출_실패_에러() {
        //given
        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                .email(email)
                .build();
        given(userService.findByEmailAndPassword(any())).willReturn(new UserFindResponseDto(User.builder().build()));
        willThrow(new IllegalArgumentException()).given(userSessionService).validateLoginEmailState(email);

        //when
        authService.login(loginRequestDto, mockHttpSession);
    }

    @Test
    public void 로그인_상태_확인_true_반환() {
        //given
        given(mockHttpSession.getAttribute("login")).willReturn(true);

        //when
        boolean result = authService.isLoggedIn(mockHttpSession);

        //then
        BDDMockito.then(mockHttpSession).should().getAttribute("login");
        then(result).isEqualTo(true);
    }

    @Test
    public void 로그인_상태_확인_false_반환() {
        //given
        given(mockHttpSession.getAttribute("login")).willReturn(null);

        //when
        boolean result = authService.isLoggedIn(mockHttpSession);

        //then
        BDDMockito.then(mockHttpSession).should().getAttribute("login");
        then(result).isEqualTo(false);
    }
}