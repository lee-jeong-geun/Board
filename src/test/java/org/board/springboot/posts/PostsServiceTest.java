package org.board.springboot.posts;

import org.assertj.core.api.BDDAssertions;
import org.board.springboot.posts.domain.Posts;
import org.board.springboot.posts.domain.PostsRepository;
import org.board.springboot.posts.dto.PostsSaveRequestDto;
import org.board.springboot.posts.service.PostsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Field;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@RunWith(MockitoJUnitRunner.class)
public class PostsServiceTest {

    @Mock
    private PostsRepository postsRepository;

    @InjectMocks
    private PostsService postsService;

    @Test
    public void 포스트_저장_호출_성공() throws Exception {
        //given
        Posts posts = Posts.builder().build();
        Field field = posts.getClass().getDeclaredField("id");
        field.setAccessible(true);
        field.set(posts, 1l);

        PostsSaveRequestDto postSaveRequestDto = PostsSaveRequestDto.builder().build();
        given(postsRepository.save(any())).willReturn(posts);

        //when
        Long id = postsService.save(postSaveRequestDto);

        //then
        then(postsRepository).should(times(1)).save(any());
        BDDAssertions.then(id).isEqualTo(1l);
    }
}
