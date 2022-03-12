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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    @Test
    public void 포스트_저장_호출_성공() throws Exception {
        //given
        String email = "jk@jk.com";
        User user = User.builder()
                .build();
        Posts posts = Posts.builder().build();
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
    public void 포스트_전체_검색_호출_성공() {
        //given
        String name = "jk";
        User user = User.builder()
                .name(name)
                .build();

        Posts posts1 = Posts.builder()
                .title("title1")
                .content("content1")
                .user(user)
                .build();

        Posts posts2 = Posts.builder()
                .user(user)
                .build();
        List<Posts> list = new ArrayList(Arrays.asList(new Posts[]{posts1, posts2}));

        given(postsRepository.findAll()).willReturn(list);


        //when
        List<PostsFindResponseDto> result = postsService.findAll();

        //then
        then(postsRepository).should(times(1)).findAll();
        BDDAssertions.then(result.size()).isEqualTo(2);
        BDDAssertions.then(result.get(0).getTitle()).isEqualTo("title1");
        BDDAssertions.then(result.get(0).getContent()).isEqualTo("content1");
        BDDAssertions.then(result.get(0).getUserName()).isEqualTo(name);
        BDDAssertions.then(result.get(1).getUserName()).isEqualTo(name);
    }
}
