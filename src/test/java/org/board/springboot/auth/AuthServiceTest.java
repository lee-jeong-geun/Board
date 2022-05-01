package org.board.springboot.auth;

import org.board.springboot.auth.dto.LoginRequestDto;
import org.board.springboot.auth.dto.LoginUserResponseDto;
import org.board.springboot.auth.service.AuthService;
import org.board.springboot.auth.service.JWTService;
import org.board.springboot.redis.user.UserSessionService;
import org.board.springboot.user.domain.User;
import org.board.springboot.user.dto.UserFindResponseDto;
import org.board.springboot.user.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockCookie;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
    private JWTService jwtService;
    @Mock
    private HttpServletRequest mockHttpServletRequest;
    @Mock
    private HttpServletResponse mockHttpServletResponse;
    @Mock
    private MockCookie mockCookie;

    @InjectMocks
    private AuthService authService;

    private final String email = "jk@jk.com";
    private final String tokenValidValue = "valid";
    private final String tokenInvalidValue = "invalid";

    @Test
    public void login_호출_성공() {
        //given
        String name = "jk";
        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                .email(email)
                .build();
        UserFindResponseDto userFindResponseDto = new UserFindResponseDto(User.builder()
                .name(name)
                .email(email)
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
        BDDMockito.then(jwtService).should().createJWT(email);
        BDDMockito.then(mockHttpServletResponse).should().addCookie(any());
        then(result.getName()).isEqualTo(name);
        then(result.getEmail()).isEqualTo(email);
    }

    @Test
    public void logout_호출_성공() {
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

    @Test(expected = IllegalArgumentException.class)
    public void logout_토큰_null_값_호출_실패_에러() {
        //given
        MockCookie[] mockCookies = new MockCookie[1];
        mockCookies[0] = mockCookie;
        given(mockHttpServletRequest.getCookies()).willReturn(mockCookies);
        given(mockCookie.getName()).willReturn(tokenInvalidValue);

        //when
        authService.logout();
    }

    @Test(expected = IllegalArgumentException.class)
    public void logout_토큰_not_null_invalid_값_호출_실패_에러() {
        //given
        MockCookie[] mockCookies = new MockCookie[1];
        mockCookies[0] = mockCookie;
        given(mockHttpServletRequest.getCookies()).willReturn(mockCookies);
        given(mockCookie.getName()).willReturn("token");
        given(mockCookie.getValue()).willReturn(tokenInvalidValue);
        given(jwtService.validateJWT(tokenInvalidValue)).willReturn(false);

        //when
        authService.logout();
    }

    @Test(expected = IllegalArgumentException.class)
    public void login_조회_실패_에러() {
        //given
        LoginRequestDto loginRequestDto = LoginRequestDto.builder().build();
        MockCookie[] mockCookies = new MockCookie[1];
        mockCookies[0] = mockCookie;

        given(mockHttpServletRequest.getCookies()).willReturn(mockCookies);
        given(mockCookie.getName()).willReturn("token");
        given(mockCookie.getValue()).willReturn(tokenValidValue);
        given(jwtService.validateJWT(tokenValidValue)).willReturn(false);
        given(userService.findByEmailAndPassword(any())).willThrow(new IllegalArgumentException("해당 유저가 없습니다."));

        //when
        authService.login(loginRequestDto);
    }

    @Test(expected = IllegalArgumentException.class)
    public void login_토큰_not_null_valid_값_호출_실패_에러() {
        //given
        LoginRequestDto loginRequestDto = LoginRequestDto.builder().build();
        MockCookie[] mockCookies = new MockCookie[1];
        mockCookies[0] = mockCookie;

        given(mockHttpServletRequest.getCookies()).willReturn(mockCookies);
        given(mockCookie.getName()).willReturn("token");
        given(mockCookie.getValue()).willReturn(tokenValidValue);
        given(jwtService.validateJWT(tokenValidValue)).willReturn(true);

        //when
        authService.login(loginRequestDto);
    }

    @Test(expected = IllegalArgumentException.class)
    public void login_이메일_로그인_상태_호출_실패_에러() {
        //given
        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                .email(email)
                .build();
        MockCookie[] mockCookies = new MockCookie[1];
        mockCookies[0] = mockCookie;

        given(mockHttpServletRequest.getCookies()).willReturn(mockCookies);
        given(mockCookie.getName()).willReturn(tokenInvalidValue);
        given(userService.findByEmailAndPassword(any())).willReturn(new UserFindResponseDto(User.builder().build()));
        willThrow(new IllegalArgumentException()).given(userSessionService).validateLoginEmailState(email);

        //when
        authService.login(loginRequestDto);
    }

    @Test
    public void isLoggedIn_true_반환() {
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
        then(result).isEqualTo(true);
    }

    @Test
    public void isLoggedIn_토큰_null_값_false_반환() {
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
        then(result).isEqualTo(false);
    }

    @Test
    public void isLoggedIn_토큰_not_null_값_invalid_값_false_반환() {
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
        then(result).isEqualTo(false);
    }
}