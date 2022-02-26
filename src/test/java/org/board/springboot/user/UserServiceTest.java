package org.board.springboot.user;

import org.board.springboot.user.domain.User;
import org.board.springboot.user.domain.UserRepository;
import org.board.springboot.user.dto.UserFindRequestDto;
import org.board.springboot.user.dto.UserFindResponseDto;
import org.board.springboot.user.dto.UserSaveRequestDto;
import org.board.springboot.user.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;


@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    public void 유저저장_호출_성공() throws Exception {
        //given
        String email = "jk@jk.com";
        User user = User.builder().build();
        Field field = user.getClass().getDeclaredField("id");
        field.setAccessible(true);
        field.set(user, 1l);

        UserSaveRequestDto userSaveRequestDto = UserSaveRequestDto.builder().email(email).build();
        given(userRepository.findByEmail(userSaveRequestDto.getEmail())).willReturn(Optional.empty());
        given(userRepository.save(any())).willReturn(user);

        //when
        Long id = userService.save(userSaveRequestDto);

        //then
        then(userRepository).should().findByEmail(email);
        then(userRepository).should().save(any());
        assertThat(id).isEqualTo(1l);
    }

    @Test
    public void 유저탐색_호출_성공() {
        //given
        String name = "jk";
        String email = "jk@jk.com";
        String password = "jkjk";
        UserFindRequestDto userFindRequestDto = UserFindRequestDto.builder()
                .email(email)
                .password(password)
                .build();
        given(userRepository.findByEmailAndPassword(email, password))
                .willReturn(Optional.of(User.builder()
                        .name(name)
                        .email(email)
                        .build()));

        //when
        UserFindResponseDto userFindResponseDto = userService.find(userFindRequestDto);

        //then
        then(userRepository).should().findByEmailAndPassword(email, password);
        assertThat(userFindResponseDto.getName()).isEqualTo(name);
        assertThat(userFindResponseDto.getEmail()).isEqualTo(email);
    }

    @Test(expected = IllegalArgumentException.class)
    public void 유저탐색_호출_에러() {
        //given
        String email = "jk@jk.com";
        String password = "jkjk";
        UserFindRequestDto userFindRequestDto = UserFindRequestDto.builder()
                .email(email)
                .password(password)
                .build();
        given(userRepository.findByEmailAndPassword(email, password)).willReturn(Optional.empty());

        //when
        userService.find(userFindRequestDto);
    }
}
