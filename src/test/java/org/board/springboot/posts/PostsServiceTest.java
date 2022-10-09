package org.board.springboot.posts;

import org.board.springboot.posts.domain.Posts;
import org.board.springboot.posts.domain.PostsRepository;
import org.board.springboot.posts.dto.PostsFindResponseDto;
import org.board.springboot.posts.dto.PostsSaveRequestDto;
import org.board.springboot.posts.service.PostsService;
import org.board.springboot.redis.posts.PostsCacheService;
import org.board.springboot.user.domain.User;
import org.board.springboot.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class PostsServiceTest {

    @Mock
    UserService userService;
    @Mock
    PostsCacheService postsCacheService;
    @Mock
    PostsRepository postsRepository;

    @InjectMocks
    PostsService postsService;

    final String email = "jk@jk.com";
    final String title = "title";
    final String content = "content";

    @Test
    void 포스트_저장_호출_성공() throws Exception {
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
        assertEquals(user, postsArgumentCaptor.getValue().getUser());
        assertEquals(1l, id);
    }

    @Test
    void 포스트_전체_검색_호출_성공() throws Exception {
        //given
        int viewCount = 0;
        User user = User.builder()
                .email(email)
                .build();

        Posts posts1 = Posts.builder()
                .title(title)
                .content(content)
                .viewCount(viewCount)
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
        assertEquals(2, result.size());
        assertEquals(1l, result.get(0).getPostsId());
        assertEquals(title, result.get(0).getTitle());
        assertEquals(content, result.get(0).getContent());
        assertEquals(viewCount, result.get(0).getViewCount());
        assertEquals(email, result.get(0).getUserEmail());
        assertEquals(2l, result.get(1).getPostsId());
        assertEquals(email, result.get(1).getUserEmail());
    }

    @Test
    void 포스트_검색_아이디_호출_성공() throws Exception {
        //given
        Long id = 1l;
        int viewCount = 0;
        User user = User.builder()
                .email(email)
                .build();
        Posts posts = Posts.builder()
                .title(title)
                .content(content)
                .viewCount(viewCount)
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
        assertEquals(id, result.getPostsId());
        assertEquals(title, result.getTitle());
        assertEquals(content, result.getContent());
        assertEquals(viewCount, result.getViewCount());
        assertEquals(email, result.getUserEmail());
    }

    @Test
    void 포스트_검색_아이디_호출_실패_에러() {
        //given
        given(postsRepository.findById(1l)).willReturn(Optional.empty());

        //when
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> postsService.findById(1l));

        //then
        assertEquals("해당 게시글이 없습니다.", exception.getMessage());
    }

    @Test
    void viewCountUpdateById_호출_성공_postsKey_존재() {
        //given
        Long id = 1l;
        int updateCount = 1;

        given(postsCacheService.hasPostsViewCountKey(id)).willReturn(true);
        given(postsCacheService.incrementPostsViewCount(id, updateCount)).willReturn(updateCount);

        //when
        int result = postsService.viewCountUpdateById(id, updateCount);

        //then
        then(postsCacheService).should().hasPostsViewCountKey(id);
        then(postsCacheService).should().incrementPostsViewCount(id, updateCount);
        assertEquals(updateCount, result);
    }

    @Test
    void viewCountUpdateById_호출_성공_postsKey_미존재_posts_존재() {
        //given
        Long id = 1l;
        int updateCount = 1;
        int viewCount = 10;

        User user = User.builder()
                .build();

        Posts posts = Posts.builder()
                .viewCount(viewCount)
                .user(user)
                .build();

        given(postsCacheService.hasPostsViewCountKey(id)).willReturn(false);
        given(postsRepository.findById(id)).willReturn(Optional.of(posts));
        given(postsCacheService.incrementPostsViewCount(id, updateCount)).willReturn(posts.getViewCount() + updateCount);

        //when
        int result = postsService.viewCountUpdateById(id, updateCount);

        //then
        then(postsCacheService).should().hasPostsViewCountKey(id);
        then(postsRepository).should().findById(id);
        then(postsCacheService).should().setPostsViewCount(id, posts.getViewCount());
        then(postsCacheService).should().incrementPostsViewCount(id, updateCount);
        assertEquals(viewCount + updateCount, result);
    }

    @Test
    void viewCountUpdateById_호출_실패_postsKey_미존재_posts_미존재() {
        //given
        Long id = 1l;
        int updateCount = 1;

        given(postsCacheService.hasPostsViewCountKey(id)).willReturn(false);
        given(postsRepository.findById(id)).willReturn(Optional.empty());

        //when
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> postsService.viewCountUpdateById(id, updateCount));

        //then
        then(postsCacheService).should().hasPostsViewCountKey(id);
        then(postsRepository).should().findById(id);
        assertEquals("해당 게시글이 없습니다.", exception.getMessage());
    }

    @Test
    void delete_호출_성공() {
        //given
        Long id = 1L;
        User user = User.builder()
                .build();
        Posts posts = Posts.builder()
                .title(title)
                .content(content)
                .user(user)
                .build();

        given(postsRepository.findById(id)).willReturn(Optional.of(posts));

        //when
        postsService.delete(id);

        //then
        then(postsRepository).should().findById(id);
        then(postsRepository).should().delete(posts);
    }

    @Test
    void delete_호출_실패_게시글_미존재_에러처리() {
        //given
        Long id = 1L;
        given(postsRepository.findById(id)).willReturn(Optional.empty());

        //when
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> postsService.delete(id));

        //then
        then(postsRepository).should().findById(id);
        assertEquals("해당 게시글이 존재하지 않습니다.", exception.getMessage());
    }
}
