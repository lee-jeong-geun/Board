package org.board.springboot.auth;

import org.board.springboot.auth.config.AuthSession;
import org.board.springboot.auth.dto.LoginRequestDto;
import org.board.springboot.auth.dto.LoginUserResponseDto;
import org.board.springboot.auth.service.AuthService;
import org.board.springboot.user.domain.User;
import org.board.springboot.user.dto.UserFindResponseDto;
import org.board.springboot.user.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpSession;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;

@RunWith(MockitoJUnitRunner.class)
public class AuthServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private MockHttpSession mockHttpSession;

    @Mock
    private AuthSession authSession;

    @InjectMocks
    private AuthService authService;

    @Test
    public void login_userService_호출_성공() {
        //given
        String name = "jk";
        String email = "jk@jk.com";
        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                .email(email)
                .build();
        UserFindResponseDto userFindResponseDto = new UserFindResponseDto(User.builder()
                .name(name)
                .email(email)
                .build());
        Map<String, Object> map = new ConcurrentHashMap<>();
        given(userService.findByEmailAndPassword(any())).willReturn(userFindResponseDto);
        given(authSession.getSession()).willReturn(map);
        given(mockHttpSession.getAttribute("login")).willReturn(null);

        //when
        authService.login(loginRequestDto, mockHttpSession);

        //then
        BDDMockito.then(userService).should(times(1)).findByEmailAndPassword(any());
        BDDMockito.then(mockHttpSession).should(times(1)).setAttribute("login", email);
        BDDMockito.then(mockHttpSession).should(times(1)).getAttribute("login");
        BDDMockito.then(authSession).should(times(2)).getSession();
    }

    @Test
    public void login_올바른값_반환() {
        //given
        String name = "jk";
        String email = "jk@jk.com";
        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                .email(email)
                .build();
        UserFindResponseDto userFindResponseDto = new UserFindResponseDto(User.builder()
                .name(name)
                .email(email)
                .build());
        Map<String, Object> map = new ConcurrentHashMap<>();
        given(userService.findByEmailAndPassword(any())).willReturn(userFindResponseDto);
        given(authSession.getSession()).willReturn(map);
        given(mockHttpSession.getAttribute("login")).willReturn(null);

        //when
        LoginUserResponseDto loginUserResponseDto = authService.login(loginRequestDto, mockHttpSession);

        //then
        BDDMockito.then(mockHttpSession).should(times(1)).setAttribute("login", email);
        BDDMockito.then(mockHttpSession).should(times(1)).getAttribute("login");
        BDDMockito.then(authSession).should(times(2)).getSession();
        then(loginUserResponseDto.getName()).isEqualTo(name);
        then(loginUserResponseDto.getEmail()).isEqualTo(email);
    }

    @Test
    public void logout_호출_성공() {
        //given
        Map<String, Object> map = new ConcurrentHashMap<>();
        given(authSession.getSession()).willReturn(map);
        given(mockHttpSession.getAttribute("login")).willReturn(true);

        //when
        boolean result = authService.logout(mockHttpSession);

        //then
        BDDMockito.then(authSession).should(times(1)).getSession();
        BDDMockito.then(mockHttpSession).should(times(2)).getAttribute("login");
        BDDMockito.then(mockHttpSession).should(times(1)).removeAttribute("login");
        then(result).isEqualTo(true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void logout_세션_null값_호출_실패_에러() {
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
    public void login_세션_not_null값_호출_실패_에러() {
        //given
        LoginRequestDto loginRequestDto = LoginRequestDto.builder().build();
        given(mockHttpSession.getAttribute("login")).willReturn(Optional.empty());

        //when
        authService.login(loginRequestDto, mockHttpSession);
    }

    @Test(expected = IllegalArgumentException.class)
    public void login_이메일_로그인_상태_호출_실패_에러() {
        //given
        String email = "jk@jk.com";
        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                .email(email)
                .build();
        Map<String, Object> map = new ConcurrentHashMap<>();
        map.put(email, true);
        given(userService.findByEmailAndPassword(any())).willReturn(new UserFindResponseDto(User.builder().build()));
        given(authSession.getSession()).willReturn(map);

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
