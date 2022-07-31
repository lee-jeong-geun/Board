package org.board.springboot.auth;

import org.board.springboot.auth.dto.LoginRequestDto;
import org.board.springboot.auth.dto.LoginUserResponseDto;
import org.board.springboot.auth.service.AuthService;
import org.board.springboot.auth.service.JWTService;
import org.board.springboot.redis.user.UserSessionService;
import org.board.springboot.user.domain.User;
import org.board.springboot.user.dto.UserFindResponseDto;
import org.board.springboot.user.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockCookie;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    UserService userService;
    @Mock
    UserSessionService userSessionService;
    @Mock
    JWTService jwtService;
    @Mock
    HttpServletRequest mockHttpServletRequest;
    @Mock
    HttpServletResponse mockHttpServletResponse;
    @Mock
    MockCookie mockCookie;

    @InjectMocks
    AuthService authService;

    final String email = "jk@jk.com";
    final String password = "jkjk";
    final String tokenValidValue = "valid";
    final String tokenInvalidValue = "invalid";

    @Test
    void login_호출_성공() {
        //given
        String name = "jk";
        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                .email(email)
                .password(password)
                .build();
        UserFindResponseDto userFindResponseDto = new UserFindResponseDto(User.builder()
                .name(name)
                .email(email)
                .password(password)
                .build());
        MockCookie[] mockCookies = new MockCookie[1];
        mockCookies[0] = mockCookie;

        given(mockHttpServletRequest.getCookies()).willReturn(mockCookies);
        given(mockCookie.getName()).willReturn("token");
        given(mockCookie.getValue()).willReturn(tokenValidValue);
        given(jwtService.validateJWT(tokenValidValue)).willReturn(false);
        given(userService.findByEmailAndPassword(any())).willReturn(userFindResponseDto);
        given(jwtService.createJWT(email)).willReturn(tokenValidValue);

        //when
        LoginUserResponseDto result = authService.login(loginRequestDto);

        //then
        BDDMockito.then(mockHttpServletRequest).should().getCookies();
        BDDMockito.then(mockCookie).should().getName();
        BDDMockito.then(mockCookie).should().getValue();
        BDDMockito.then(jwtService).should().validateJWT(mockCookie.getValue());
        BDDMockito.then(userService).should().findByEmailAndPassword(any());
        BDDMockito.then(userSessionService).should().validateLoginEmailState(email);
        BDDMockito.then(userSessionService).should().createLoginState(email);
        BDDMockito.then(userService).should().updateLastLoginTime(email);
        BDDMockito.then(jwtService).should().createJWT(email);
        BDDMockito.then(mockHttpServletResponse).should().addCookie(any());
        assertEquals(name, result.getName());
        assertEquals(email, result.getEmail());
    }

    @Test
    void logout_호출_성공() {
        //given
        MockCookie[] mockCookies = new MockCookie[1];
        mockCookies[0] = mockCookie;
        given(mockHttpServletRequest.getCookies()).willReturn(mockCookies);
        given(mockCookie.getName()).willReturn("token");
        given(mockCookie.getValue()).willReturn(tokenValidValue);
        given(jwtService.validateJWT(tokenValidValue)).willReturn(true);
        given(jwtService.getEmail(tokenValidValue)).willReturn(email);

        //when
        boolean result = authService.logout();

        //then
        BDDMockito.then(mockHttpServletRequest).should().getCookies();
        BDDMockito.then(mockCookie).should().getName();
        BDDMockito.then(mockCookie).should(times(2)).getValue();
        BDDMockito.then(jwtService).should().validateJWT(tokenValidValue);
        BDDMockito.then(jwtService).should().getEmail(tokenValidValue);
        BDDMockito.then(mockCookie).should().setPath("/");
        BDDMockito.then(mockCookie).should().setMaxAge(0);
        BDDMockito.then(userSessionService).should().deleteLoginState(email);
        BDDMockito.then(mockCookie).should().setMaxAge(0);
        BDDMockito.then(mockHttpServletResponse).should().addCookie(mockCookie);
        then(result).isEqualTo(true);
    }

    @Test
    void logout_토큰_null_값_호출_실패_에러() {
        //given
        MockCookie[] mockCookies = new MockCookie[1];
        mockCookies[0] = mockCookie;
        given(mockHttpServletRequest.getCookies()).willReturn(mockCookies);
        given(mockCookie.getName()).willReturn(tokenInvalidValue);

        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> authService.logout());

        //then
        assertEquals("로그인 상태가 아닙니다.", exception.getMessage());
    }

    @Test
    void logout_토큰_not_null_invalid_값_호출_실패_에러() {
        //given
        MockCookie[] mockCookies = new MockCookie[1];
        mockCookies[0] = mockCookie;
        given(mockHttpServletRequest.getCookies()).willReturn(mockCookies);
        given(mockCookie.getName()).willReturn("token");
        given(mockCookie.getValue()).willReturn(tokenInvalidValue);
        given(jwtService.validateJWT(tokenInvalidValue)).willReturn(false);

        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> authService.logout());

        //then
        assertEquals("로그인 상태가 아닙니다.", exception.getMessage());
    }

    @Test
    void login_조회_실패_에러() {
        //given
        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                .email(email)
                .password(password)
                .build();
        MockCookie[] mockCookies = new MockCookie[1];
        mockCookies[0] = mockCookie;

        given(mockHttpServletRequest.getCookies()).willReturn(mockCookies);
        given(mockCookie.getName()).willReturn("token");
        given(mockCookie.getValue()).willReturn(tokenValidValue);
        given(jwtService.validateJWT(tokenValidValue)).willReturn(false);
        given(userService.findByEmailAndPassword(any())).willThrow(new IllegalArgumentException("해당 유저가 없습니다."));

        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> authService.login(loginRequestDto));

        //then
        assertEquals("해당 유저가 없습니다.", exception.getMessage());
    }

    @Test
    void login_토큰_not_null_valid_값_호출_실패_에러() {
        //given
        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                .email(email)
                .password(password)
                .build();
        MockCookie[] mockCookies = new MockCookie[1];
        mockCookies[0] = mockCookie;

        given(mockHttpServletRequest.getCookies()).willReturn(mockCookies);
        given(mockCookie.getName()).willReturn("token");
        given(mockCookie.getValue()).willReturn(tokenValidValue);
        given(jwtService.validateJWT(tokenValidValue)).willReturn(true);

        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> authService.login(loginRequestDto));

        //then
        assertEquals("이미 로그인 상태입니다.", exception.getMessage());
    }

    @Test
    void login_이메일_로그인_상태_호출_실패_에러() {
        //given
        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                .email(email)
                .password(password)
                .build();
        MockCookie[] mockCookies = new MockCookie[1];
        mockCookies[0] = mockCookie;

        given(mockHttpServletRequest.getCookies()).willReturn(mockCookies);
        given(mockCookie.getName()).willReturn(tokenInvalidValue);
        given(userService.findByEmailAndPassword(any())).willReturn(new UserFindResponseDto(User.builder().build()));
        willThrow(new IllegalArgumentException("Exception")).given(userSessionService).validateLoginEmailState(email);

        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> authService.login(loginRequestDto));

        //then
        assertEquals("Exception", exception.getMessage());
    }

    @Test
    void isLoggedIn_true_반환() {
        //given
        MockCookie[] mockCookies = new MockCookie[1];
        mockCookies[0] = mockCookie;

        given(mockHttpServletRequest.getCookies()).willReturn(mockCookies);
        given(mockCookie.getName()).willReturn("token");
        given(mockCookie.getValue()).willReturn(tokenValidValue);
        given(jwtService.validateJWT(tokenValidValue)).willReturn(true);

        //when
        boolean result = authService.isLoggedIn();

        //then
        BDDMockito.then(mockHttpServletRequest).should().getCookies();
        BDDMockito.then(mockCookie).should().getName();
        BDDMockito.then(mockCookie).should().getValue();
        BDDMockito.then(jwtService).should().validateJWT(tokenValidValue);
        assertEquals(true, result);
    }

    @Test
    void isLoggedIn_토큰_null_값_false_반환() {
        //given
        MockCookie[] mockCookies = new MockCookie[1];
        mockCookies[0] = mockCookie;

        given(mockHttpServletRequest.getCookies()).willReturn(mockCookies);
        given(mockCookie.getName()).willReturn(tokenInvalidValue);

        //when
        boolean result = authService.isLoggedIn();

        //then
        BDDMockito.then(mockHttpServletRequest).should().getCookies();
        BDDMockito.then(mockCookie).should().getName();
        assertEquals(false, result);
    }

    @Test
    void isLoggedIn_토큰_not_null_값_invalid_값_false_반환() {
        //given
        MockCookie[] mockCookies = new MockCookie[1];
        mockCookies[0] = mockCookie;

        given(mockHttpServletRequest.getCookies()).willReturn(mockCookies);
        given(mockCookie.getName()).willReturn("token");
        given(mockCookie.getValue()).willReturn(tokenInvalidValue);
        given(jwtService.validateJWT(tokenInvalidValue)).willReturn(false);

        //when
        boolean result = authService.isLoggedIn();

        //then
        BDDMockito.then(mockHttpServletRequest).should().getCookies();
        BDDMockito.then(mockCookie).should().getName();
        BDDMockito.then(mockCookie).should().getValue();
        BDDMockito.then(jwtService).should().validateJWT(tokenInvalidValue);
        assertEquals(false, result);
    }

    @Test
    public void login_email_empty_값_호출_실패_에러() {
        //given
        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                .email("")
                .build();

        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> authService.login(loginRequestDto));

        //then
        assertEquals("아이디를 입력해주세요.", exception.getMessage());
    }

    @Test
    void login_email_null_값_호출_실패_에러() {
        //given
        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                .email(null)
                .build();

        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> authService.login(loginRequestDto));

        //then
        assertEquals("아이디를 입력해주세요.", exception.getMessage());
    }

    @Test
    void login_password_empty_값_호출_실패_에러() {
        //given
        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                .email(email)
                .password("")
                .build();

        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> authService.login(loginRequestDto));

        //then
        assertEquals("비밀번호를 입력해주세요.", exception.getMessage());
    }

    @Test
    void login_password_null_값_호출_실패_에러() {
        //given
        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                .email(email)
                .password(null)
                .build();

        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> authService.login(loginRequestDto));

        //then
        assertEquals("비밀번호를 입력해주세요.", exception.getMessage());
    }

    @Test
    void login_호출_실패_updateLastLoginTime_에러() {
        //given
        String name = "jk";
        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                .email(email)
                .password(password)
                .build();
        UserFindResponseDto userFindResponseDto = new UserFindResponseDto(User.builder()
                .name(name)
                .email(email)
                .password(password)
                .build());
        MockCookie[] mockCookies = new MockCookie[1];
        mockCookies[0] = mockCookie;

        given(mockHttpServletRequest.getCookies()).willReturn(mockCookies);
        given(mockCookie.getName()).willReturn("token");
        given(mockCookie.getValue()).willReturn(tokenValidValue);
        given(jwtService.validateJWT(tokenValidValue)).willReturn(false);
        given(userService.findByEmailAndPassword(any())).willReturn(userFindResponseDto);
        willThrow(new RuntimeException("Runtime Exception")).given(userService).updateLastLoginTime(email);

        //when
        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.login(loginRequestDto));

        //then
        assertEquals("Runtime Exception", exception.getMessage());
    }
}