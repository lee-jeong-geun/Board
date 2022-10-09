package org.board.springboot.redis;

import org.board.springboot.redis.posts.PostsCacheService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
public class PostsCacheServiceTest {

    @Mock
    RedisTemplate<String, Object> redisTemplate;
    @Mock
    ValueOperations<String, Object> valueOperations;

    @InjectMocks
    PostsCacheService postsCacheService;

    String VIEW_COUNT = "postsViewCount";

    @Test
    void hasPostsViewCountKey_호출_성공_키_존재() {
        //given
        Long postsId = 1L;
        String key = VIEW_COUNT + ":" + postsId;

        given(redisTemplate.hasKey(key)).willReturn(true);

        //when
        boolean result = postsCacheService.hasPostsViewCountKey(postsId);

        //then
        assertTrue(result);
        then(redisTemplate).should().hasKey(key);
    }

    @Test
    void hasPostsViewCountKey_호출_성공_키_미존재() {
        //given
        Long postsId = 1L;
        String key = VIEW_COUNT + ":" + postsId;

        given(redisTemplate.hasKey(key)).willReturn(false);

        //when
        boolean result = postsCacheService.hasPostsViewCountKey(postsId);

        //then
        assertFalse(result);
        then(redisTemplate).should().hasKey(key);
    }

    @Test
    void setPostsViewCount_호출_성공() {
        //given
        Long postsId = 1L;
        int viewCount = 0;
        String key = VIEW_COUNT + ":" + postsId;
        Object value = String.valueOf(viewCount);

        given(redisTemplate.opsForValue()).willReturn(valueOperations);

        //when
        postsCacheService.setPostsViewCount(postsId, viewCount);

        //then
        then(redisTemplate).should().opsForValue();
        then(valueOperations).should().set(key, value);
    }

    @Test
    void incrementPostsViewCount_호출_성공() {
        //given
        Long postsId = 1L;
        int updateCount = 1;
        String key = VIEW_COUNT + ":" + postsId;

        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.increment(key, updateCount)).willReturn(1L);

        //when
        int result = postsCacheService.incrementPostsViewCount(postsId, updateCount);

        //then
        assertEquals(1, result);
        then(redisTemplate).should().opsForValue();
        then(valueOperations).should().increment(key, updateCount);
    }
}
