package org.board.springboot.auth;

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

    @InjectMocks
    private AuthService authService;

    @Test
    public void login_userService_호출_성공() {
        //given
        LoginRequestDto loginRequestDto = LoginRequestDto.builder().build();
        UserFindResponseDto userFindResponseDto = new UserFindResponseDto(User.builder().build());

        given(userService.find(any())).willReturn(userFindResponseDto);
        //when
        authService.login(loginRequestDto, mockHttpSession);

        //then
        BDDMockito.then(userService).should(times(1)).find(any());
        BDDMockito.then(mockHttpSession).should(times(1)).setAttribute("login", true);
    }

    @Test
    public void login_올바른값_반환() {
        //given
        String name = "jk";
        String email = "jk@jk.com";
        LoginRequestDto loginRequestDto = LoginRequestDto.builder().build();
        UserFindResponseDto userFindResponseDto = new UserFindResponseDto(User.builder()
                .name(name)
                .email(email)
                .build());
        given(userService.find(any())).willReturn(userFindResponseDto);

        //when
        LoginUserResponseDto loginUserResponseDto = authService.login(loginRequestDto, mockHttpSession);

        //then
        BDDMockito.then(mockHttpSession).should(times(1)).setAttribute("login", true);
        then(loginUserResponseDto.getName()).isEqualTo(name);
        then(loginUserResponseDto.getEmail()).isEqualTo(email);
    }

    @Test
    public void logout_호출_성공() {
        //given
        given(mockHttpSession.getAttribute("login")).willReturn(true);

        //when
        boolean result = authService.logout(mockHttpSession);

        //then
        BDDMockito.then(mockHttpSession).should(times(2)).getAttribute("login");
        then(result).isEqualTo(true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void logout_null값_호출_실패_에러() {
        //given
        given(mockHttpSession.getAttribute("login")).willReturn(null);

        //when
        authService.logout(mockHttpSession);
    }

    @Test(expected = IllegalArgumentException.class)
    public void logout_false값_호출_실패_에러() {
        //given
        given(mockHttpSession.getAttribute("login")).willReturn(false);

        //when
        authService.logout(mockHttpSession);
    }

    @Test(expected = IllegalArgumentException.class)
    public void login_조회_실패_에러() {
        //given
        LoginRequestDto loginRequestDto = LoginRequestDto.builder().build();
        given(userService.find(any())).willThrow(new IllegalArgumentException("해당 유저가 없습니다."));

        //when
        authService.login(loginRequestDto, mockHttpSession);
    }
}
