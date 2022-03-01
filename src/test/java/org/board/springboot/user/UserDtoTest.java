package org.board.springboot.user;

import org.board.springboot.user.domain.User;
import org.board.springboot.user.dto.UserSaveRequestDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

public class UserDtoTest {

    @Test
    public void SaveRequestDto_toEntity_성공() {
        //given
        String name = "jk";
        String email = "jk@jk.com";
        String password = "jkjk";
        UserSaveRequestDto userSaveRequestDto = UserSaveRequestDto.builder()
                .name(name)
                .email(email)
                .password(password)
                .build();

        //when
        User user = userSaveRequestDto.toEntity();

        //then
        assertThat(user.getName()).isEqualTo(name);
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getPassword()).isEqualTo(password);
    }

}
