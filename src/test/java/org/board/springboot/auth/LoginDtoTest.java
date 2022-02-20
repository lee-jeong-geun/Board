package org.board.springboot.auth;

import org.board.springboot.auth.dto.LoginRequestDto;
import org.board.springboot.user.dto.UserFindRequestDto;
import org.junit.Test;

import static org.assertj.core.api.BDDAssertions.then;


public class LoginDtoTest {

    @Test
    public void LoginRequestDto_toUserFindRequestDto_변환_성공() {
        //given
        String email = "jk@jk.com";
        String password = "jkjk";
        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                .email(email)
                .password(password)
                .build();

        //when
        UserFindRequestDto userFindRequestDto = loginRequestDto.toUserFindRequestDto();

        //then
        then(userFindRequestDto.getEmail()).isEqualTo(email);
        then(userFindRequestDto.getPassword()).isEqualTo(password);
    }
}
