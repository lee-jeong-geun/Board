package org.board.springboot.user;

import org.board.springboot.user.domain.User;
import org.board.springboot.user.domain.UserRepository;
import org.board.springboot.user.dto.UserFindRequestDto;
import org.board.springboot.user.dto.UserSaveRequestDto;
import org.board.springboot.user.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

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
    public void 유저저장_호출_성공() {
        //given
        UserSaveRequestDto userSaveRequestDto = UserSaveRequestDto.builder().build();
        given(userRepository.save(any())).willReturn(userSaveRequestDto.toEntity());

        //when
        userService.save(userSaveRequestDto);

        //then
        then(userRepository).should().save(any());
    }

    @Test
    public void 유저탐색_호출_성공() {
        //given
        String email = "jk@jk.com";
        String password = "jkjk";
        UserFindRequestDto userFindRequestDto = UserFindRequestDto.builder()
                .email(email)
                .password(password)
                .build();
        given(userRepository.findByEmailAndPassword(email, password))
                .willReturn(Optional.of(User.builder().build()));

        //when
        userService.find(userFindRequestDto);

        //then
        then(userRepository).should().findByEmailAndPassword(email, password);
    }
}
