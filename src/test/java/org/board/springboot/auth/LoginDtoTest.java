package org.board.springboot.auth;

import org.board.springboot.auth.dto.LoginRequestDto;
import org.board.springboot.user.dto.UserFindRequestDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class LoginDtoTest {

    @Test
    void LoginRequestDto_toUserFindRequestDto_변환_성공() {
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
        assertEquals(email, userFindRequestDto.getEmail());
        assertEquals(password, userFindRequestDto.getPassword());
    }
}
