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

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;

@RunWith(MockitoJUnitRunner.class)
public class AuthServiceTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthService authService;

    @Test
    public void login_userService_호출_성공() {
        //given
        LoginRequestDto loginRequestDto = LoginRequestDto.builder().build();
        UserFindResponseDto userFindResponseDto = new UserFindResponseDto(User.builder().build());

        given(userService.find(any())).willReturn(userFindResponseDto);

        //when
        authService.login(loginRequestDto);

        //then
        BDDMockito.then(userService).should(times(1)).find(any());
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
        LoginUserResponseDto loginUserResponseDto = authService.login(loginRequestDto);

        //then
        then(loginUserResponseDto.getName()).isEqualTo(name);
        then(loginUserResponseDto.getEmail()).isEqualTo(email);
    }

    @Test(expected = IllegalArgumentException.class)
    public void login_조회_실패_에러() {
        //given
        LoginRequestDto loginRequestDto = LoginRequestDto.builder().build();
        given(userService.find(any())).willThrow(new IllegalArgumentException("해당 유저가 없습니다."));

        //when
        authService.login(loginRequestDto);
    }
}
