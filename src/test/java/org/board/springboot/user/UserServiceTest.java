package org.board.springboot.user;

import org.board.springboot.posts.domain.Posts;
import org.board.springboot.user.domain.User;
import org.board.springboot.user.domain.UserRepository;
import org.board.springboot.user.dto.*;
import org.board.springboot.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserService userService;

    final String name = "jkjk";
    final String email = "jk@jk.com";
    final String password = "jkjk";

    @Test
    void 유저저장_호출_성공() throws Exception {
        //given
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
        assertEquals(1l, id);
    }

    @Test
    void 유저저장_호출_에러() {
        //given
        UserSaveRequestDto userSaveRequestDto = UserSaveRequestDto.builder().email(email).build();
        given(userRepository.findByEmail(userSaveRequestDto.getEmail())).willReturn(Optional.of(User.builder().build()));

        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.save(userSaveRequestDto));

        //then
        assertEquals("해당 유저가 이미 존재합니다.", exception.getMessage());
    }

    @Test
    void 유저탐색_호출_성공() {
        //given
        String name = "jk";
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
        UserFindResponseDto userFindResponseDto = userService.findByEmailAndPassword(userFindRequestDto);

        //then
        then(userRepository).should().findByEmailAndPassword(email, password);
        assertEquals(name, userFindResponseDto.getName());
        assertEquals(email, userFindResponseDto.getEmail());
    }

    @Test
    void 유저탐색_호출_에러() {
        //given
        UserFindRequestDto userFindRequestDto = UserFindRequestDto.builder()
                .email(email)
                .password(password)
                .build();
        given(userRepository.findByEmailAndPassword(email, password)).willReturn(Optional.empty());

        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.findByEmailAndPassword(userFindRequestDto));

        //then

        assertEquals("해당 유저가 없습니다.", exception.getMessage());
    }

    @Test
    void 유저탐색_이메일_호출_성공() {
        //given
        User user = User.builder()
                .email(email)
                .build();
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));

        //when
        User result = userService.findByEmail(email);

        //then
        then(userRepository).should().findByEmail(email);
        assertEquals(user, result);
    }

    @Test
    void 유저탐색_이메일_호출_실패_에러() {
        //given
        given(userRepository.findByEmail(email)).willReturn(Optional.empty());

        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.findByEmail(email));

        //then
        assertEquals("해당 유저가 없습니다.", exception.getMessage());
    }

    @Test
    void 유저_게시글_조회_호출_성공() {
        //given
        User user = User.builder()
                .email(email)
                .build();
        Posts posts1 = Posts.builder()
                .title("title1")
                .content("content1")
                .user(user)
                .build();
        Posts posts2 = Posts.builder()
                .title("title2")
                .content("content2")
                .user(user)
                .build();

        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));

        //when
        List<UserFindPostsListResponseDto> result = userService.findPostsByEmail(email);

        //then
        then(userRepository).should().findByEmail(email);
        assertEquals(2, result.size());
        assertEquals("title1", result.get(0).getTitle());
        assertEquals("content1", result.get(0).getContent());
        assertEquals("title2", result.get(1).getTitle());
        assertEquals("content2", result.get(1).getContent());
    }

    @Test
    void 유저_게시글_조회_호출_실패_에러() {
        //given
        given(userRepository.findByEmail(email)).willReturn(Optional.empty());

        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.findPostsByEmail(email));

        //then
        assertEquals("해당 유저가 없습니다.", exception.getMessage());
    }

    @Test
    void 유저_로그인_시간_업데이트_호출_성공() {
        //given
        User user = User.builder()
                .email(email)
                .build();

        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));

        //when
        userService.updateLastLoginTime(email);

        //then
        then(userRepository).should().findByEmail(email);
        assertNotNull(user.getLastLogIn());
    }

    @Test
    void 유저_로그인_시간_업데이트_호출_실패_에러() {
        //given
        given(userRepository.findByEmail(email)).willReturn(Optional.empty());

        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.updateLastLoginTime(email));

        //then
        assertEquals("해당 유저가 없습니다.", exception.getMessage());
    }

    @Test
    void 유저_정보_업데이트_호출_성공() {
        //given
        String updateName = "jkjk2";
        String updatePassword = "jkjk2";

        User user = User.builder()
                .name(name)
                .email(email)
                .password(password)
                .build();

        UserUpdateModel userUpdateModel = UserUpdateModel.builder()
                .name(updateName)
                .email(email)
                .password(updatePassword)
                .build();

        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));

        //when
        userService.updateUser(userUpdateModel);

        //then
        then(userRepository).should().findByEmail(email);
        assertEquals(updateName, user.getName());
        assertEquals(updatePassword, user.getPassword());
    }

    @Test
    void 유저_정보_업데이트_호출_실패_에러() {
        //given
        String updateName = "jkjk2";
        String updatePassword = "jkjk2";

        UserUpdateModel userUpdateModel = UserUpdateModel.builder()
                .name(updateName)
                .email(email)
                .password(updatePassword)
                .build();

        given(userRepository.findByEmail(email)).willReturn(Optional.empty());

        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.updateUser(userUpdateModel));

        //then
        then(userRepository).should().findByEmail(email);
        assertEquals("해당 유저가 없습니다.", exception.getMessage());
    }
}
