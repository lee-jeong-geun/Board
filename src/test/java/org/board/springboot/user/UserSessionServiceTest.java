package org.board.springboot.user;

import org.board.springboot.user.service.UserSessionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@RunWith(MockitoJUnitRunner.class)
public class UserSessionServiceTest {

    @Mock
    private HashOperations<String, Object, Object> hashOperations;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @InjectMocks
    private UserSessionService userSessionService;

    private static final int TODAY_POSTS_COUNT_MAX = 10;
    private static final String TODAY_REMAIN_POSTS_COUNT = "todayRemainPostsCount";

    @Test
    public void checkTodayRemainPostsCount_호출_성공() {
        //given
        String email = "jk@jk.com";
        given(redisTemplate.opsForHash()).willReturn(hashOperations);
        given(hashOperations.hasKey(email, TODAY_REMAIN_POSTS_COUNT)).willReturn(true);
        given(hashOperations.get(email, TODAY_REMAIN_POSTS_COUNT)).willReturn(TODAY_POSTS_COUNT_MAX);

        //when
        userSessionService.checkTodayRemainPostsCount(email);

        //then
        then(redisTemplate).should(times(2)).opsForHash();
        then(hashOperations).should().hasKey(email, TODAY_REMAIN_POSTS_COUNT);
        then(hashOperations).should().get(email, TODAY_REMAIN_POSTS_COUNT);
    }

    @Test(expected = IllegalStateException.class)
    public void checkTodayRemainPostsCount_호출_실패_에러처리() {
        //given
        String email = "jk@jk.com";
        given(redisTemplate.opsForHash()).willReturn(hashOperations);
        given(hashOperations.hasKey(email, TODAY_REMAIN_POSTS_COUNT)).willReturn(true);
        given(hashOperations.get(email, TODAY_REMAIN_POSTS_COUNT)).willReturn(0);

        //when
        userSessionService.checkTodayRemainPostsCount(email);
    }
}
