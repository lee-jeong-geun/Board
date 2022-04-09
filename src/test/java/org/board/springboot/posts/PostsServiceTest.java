package org.board.springboot.posts;

import org.assertj.core.api.BDDAssertions;
import org.board.springboot.posts.domain.Posts;
import org.board.springboot.posts.domain.PostsRepository;
import org.board.springboot.posts.dto.PostsFindResponseDto;
import org.board.springboot.posts.dto.PostsSaveRequestDto;
import org.board.springboot.posts.service.PostsService;
import org.board.springboot.user.domain.User;
import org.board.springboot.user.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@RunWith(MockitoJUnitRunner.class)
public class PostsServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private PostsRepository postsRepository;

    @InjectMocks
    private PostsService postsService;

    private final String email = "jk@jk.com";
    private final String title = "title";
    private final String content = "content";

    @Test
    public void 포스트_저장_호출_성공() throws Exception {
        //given
        User user = User.builder()
                .build();
        Posts posts = Posts.builder()
                .user(user)
                .build();
        Field field = posts.getClass().getDeclaredField("id");
        field.setAccessible(true);
        field.set(posts, 1l);

        PostsSaveRequestDto postSaveRequestDto = PostsSaveRequestDto.builder()
                .email(email)
                .build();
        ArgumentCaptor<Posts> postsArgumentCaptor = ArgumentCaptor.forClass(Posts.class);
        given(userService.findByEmail(postSaveRequestDto.getEmail())).willReturn(user);
        given(postsRepository.save(any())).willReturn(posts);

        //when
        Long id = postsService.save(postSaveRequestDto);

        //then
        then(userService).should().findByEmail(postSaveRequestDto.getEmail());
        then(postsRepository).should().save(postsArgumentCaptor.capture());
        BDDAssertions.then(postsArgumentCaptor.getValue().getUser()).isEqualTo(user);
        BDDAssertions.then(id).isEqualTo(1l);
    }

    @Test
    public void 포스트_전체_검색_호출_성공() throws Exception {
        //given
        User user = User.builder()
                .email(email)
                .build();

        Posts posts1 = Posts.builder()
                .title(title)
                .content(content)
                .user(user)
                .build();
        Field field = posts1.getClass().getDeclaredField("id");
        field.setAccessible(true);
        field.set(posts1, 1l);

        Posts posts2 = Posts.builder()
                .user(user)
                .build();
        field = posts2.getClass().getDeclaredField("id");
        field.setAccessible(true);
        field.set(posts2, 2l);
        List<Posts> list = new ArrayList(Arrays.asList(posts1, posts2));

        given(postsRepository.findAll()).willReturn(list);


        //when
        List<PostsFindResponseDto> result = postsService.findAll();

        //then
        then(postsRepository).should(times(1)).findAll();
        BDDAssertions.then(result.size()).isEqualTo(2);
        BDDAssertions.then(result.get(0).getPostsId()).isEqualTo(1l);
        BDDAssertions.then(result.get(0).getTitle()).isEqualTo(title);
        BDDAssertions.then(result.get(0).getContent()).isEqualTo(content);
        BDDAssertions.then(result.get(0).getUserEmail()).isEqualTo(email);
        BDDAssertions.then(result.get(1).getPostsId()).isEqualTo(2l);
        BDDAssertions.then(result.get(1).getUserEmail()).isEqualTo(email);
    }

    @Test
    public void 포스트_검색_아이디_호출_성공() throws Exception {
        //given
        Long id = 1l;
        User user = User.builder()
                .email(email)
                .build();
        Posts posts = Posts.builder()
                .title(title)
                .content(content)
                .user(user)
                .build();
        Field field = posts.getClass().getDeclaredField("id");
        field.setAccessible(true);
        field.set(posts, id);
        given(postsRepository.findById(id)).willReturn(Optional.of(posts));

        //when
        PostsFindResponseDto result = postsService.findById(id);

        //then
        then(postsRepository).should().findById(id);
        BDDAssertions.then(result.getPostsId()).isEqualTo(id);
        BDDAssertions.then(result.getTitle()).isEqualTo(title);
        BDDAssertions.then(result.getContent()).isEqualTo(content);
        BDDAssertions.then(result.getUserEmail()).isEqualTo(email);
    }

    @Test(expected = IllegalStateException.class)
    public void 포스트_검색_아이디_호출_실패_에러() {
        //given
        given(postsRepository.findById(1l)).willReturn(Optional.empty());

        //when
        postsService.findById(1l);
    }
}
