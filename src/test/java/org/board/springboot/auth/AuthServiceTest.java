package org.board.springboot.auth;

import org.board.springboot.auth.dto.LoginUserResponseDto;
import org.board.springboot.auth.service.AuthService;
import org.board.springboot.user.domain.User;
import org.board.springboot.user.dto.UserFindResponseDto;
import org.board.springboot.user.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.BDDAssertions.then;

@RunWith(MockitoJUnitRunner.class)
public class AuthServiceTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthService authService;

    @Test
    public void getLoginUserResponseDto_호출_올바른값_반환() {
        //given
        String name = "jk";
        String email = "jk@jk.com";
        User user = User.builder()
                .name(name)
                .email(email)
                .build();
        UserFindResponseDto userFindResponseDto = new UserFindResponseDto(user);

        //when
        LoginUserResponseDto loginUserResponseDto = authService.getLoginUserResponseDto(userFindResponseDto);

        //then
        then(loginUserResponseDto.getName()).isEqualTo(name);
        then(loginUserResponseDto.getEmail()).isEqualTo(email);
    }

    @Test
    public void getLoginUserResponseDto_호출_null값_반환() {
        //given
        UserFindResponseDto userFindResponseDto = null;

        //when
        LoginUserResponseDto loginUserResponseDto = authService.getLoginUserResponseDto(userFindResponseDto);

        //then
        then(loginUserResponseDto).isNull();
    }
}
