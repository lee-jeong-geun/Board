package org.board.springboot.user;

import org.assertj.core.api.BDDAssertions;
import org.board.springboot.posts.domain.Posts;
import org.board.springboot.user.domain.User;
import org.board.springboot.user.domain.UserRepository;
import org.board.springboot.user.dto.UserFindPostsListResponseDto;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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

    @Test(expected = IllegalArgumentException.class)
    public void 유저저장_호출_에러() {
        //given
        String email = "jk@jk.com";
        UserSaveRequestDto userSaveRequestDto = UserSaveRequestDto.builder().email(email).build();
        given(userRepository.findByEmail(userSaveRequestDto.getEmail())).willReturn(Optional.of(User.builder().build()));

        //when
        userService.save(userSaveRequestDto);
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

    @Test
    public void 유저탐색_이메일_호출_성공() {
        //given
        String email = "jk@jk.com";
        User user = User.builder()
                .email(email)
                .build();
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));

        //when
        User result = userService.findByEmail(email);

        //then
        then(userRepository).should().findByEmail(email);
        BDDAssertions.then(user).isEqualTo(result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void 유저탐색_이메일_호출_실패_에러() {
        //given
        String email = "jk@jk.com";
        given(userRepository.findByEmail(email)).willReturn(Optional.empty());

        //when
        userService.findByEmail(email);
    }

    @Test
    public void 유저_게시글_조회_호출_성공() {
        //given
        String email = "jk@jk.com";
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
        BDDAssertions.then(result.size()).isEqualTo(2);
        BDDAssertions.then(result.get(0).getTitle()).isEqualTo("title1");
        BDDAssertions.then(result.get(0).getContent()).isEqualTo("content1");
        BDDAssertions.then(result.get(1).getTitle()).isEqualTo("title2");
        BDDAssertions.then(result.get(1).getContent()).isEqualTo("content2");
    }

    @Test(expected = IllegalArgumentException.class)
    public void 유저_게시글_조회_호출_실패_에러() {
        //given
        String email = "jk@jk.com";
        given(userRepository.findByEmail(email)).willReturn(Optional.empty());

        //when
        userService.findPostsByEmail(email);
    }
}
