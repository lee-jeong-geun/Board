package org.board.springboot.redis;

import org.board.springboot.redis.user.UserSessionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDateTime;

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

    private final int TODAY_POSTS_COUNT_MAX = 10;
    private final int POSTS_SAVE_INTERVAL_TIME = 5;
    private final String TODAY_REMAIN_POSTS_COUNT = "todayRemainPostsCount";
    private final String LAST_POSTS_SAVE_TIME = "lastPostsSaveTime";
    private final String email = "jk@jk.com";

    @Test
    public void checkTodayRemainPostsCount_호출_성공() {
        //given
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

    @Test
    public void checkTodayRemainPostsCount_호출_성공_hasKey_값_false() {
        //given
        given(redisTemplate.opsForHash()).willReturn(hashOperations);
        given(hashOperations.hasKey(email, TODAY_REMAIN_POSTS_COUNT)).willReturn(false);

        //when
        userSessionService.checkTodayRemainPostsCount(email);

        //then
        then(redisTemplate).should().opsForHash();
        then(hashOperations).should().hasKey(email, TODAY_REMAIN_POSTS_COUNT);
    }

    @Test(expected = IllegalStateException.class)
    public void checkTodayRemainPostsCount_호출_실패_에러처리() {
        //given
        given(redisTemplate.opsForHash()).willReturn(hashOperations);
        given(hashOperations.hasKey(email, TODAY_REMAIN_POSTS_COUNT)).willReturn(true);
        given(hashOperations.get(email, TODAY_REMAIN_POSTS_COUNT)).willReturn(0);

        //when
        userSessionService.checkTodayRemainPostsCount(email);
    }

    @Test
    public void updateTodayRemainPostsCount_호출_성공_hasKey_값_false() {
        //given
        given(redisTemplate.opsForHash()).willReturn(hashOperations);
        given(hashOperations.hasKey(email, TODAY_REMAIN_POSTS_COUNT)).willReturn(false);
        given(hashOperations.get(email, TODAY_REMAIN_POSTS_COUNT)).willReturn(TODAY_POSTS_COUNT_MAX);

        //when
        userSessionService.updateTodayRemainPostsCount(email);

        //then
        then(redisTemplate).should(times(4)).opsForHash();
        then(hashOperations).should().hasKey(email, TODAY_REMAIN_POSTS_COUNT);
        then(hashOperations).should().put(email, TODAY_REMAIN_POSTS_COUNT, String.valueOf(TODAY_POSTS_COUNT_MAX));
        then(hashOperations).should().get(email, TODAY_REMAIN_POSTS_COUNT);
        then(hashOperations).should().put(email, TODAY_REMAIN_POSTS_COUNT, String.valueOf(TODAY_POSTS_COUNT_MAX - 1));
    }

    @Test
    public void updateTodayRemainPostsCount_호출_성공_hasKey_값_true() {
        //given
        given(redisTemplate.opsForHash()).willReturn(hashOperations);
        given(hashOperations.hasKey(email, TODAY_REMAIN_POSTS_COUNT)).willReturn(true);
        given(hashOperations.get(email, TODAY_REMAIN_POSTS_COUNT)).willReturn(TODAY_POSTS_COUNT_MAX);

        //when
        userSessionService.updateTodayRemainPostsCount(email);

        //then
        then(redisTemplate).should(times(3)).opsForHash();
        then(hashOperations).should().hasKey(email, TODAY_REMAIN_POSTS_COUNT);
        then(hashOperations).should().get(email, TODAY_REMAIN_POSTS_COUNT);
        then(hashOperations).should().put(email, TODAY_REMAIN_POSTS_COUNT, String.valueOf(TODAY_POSTS_COUNT_MAX - 1));
    }

    @Test
    public void updateTodayRemainPostsCount_호출_성공_hasKey_값_true_remain_값_1() {
        //given
        int todayRemainPostsCount = 1;
        given(redisTemplate.opsForHash()).willReturn(hashOperations);
        given(hashOperations.hasKey(email, TODAY_REMAIN_POSTS_COUNT)).willReturn(true);
        given(hashOperations.get(email, TODAY_REMAIN_POSTS_COUNT)).willReturn(todayRemainPostsCount);

        //when
        userSessionService.updateTodayRemainPostsCount(email);

        //then
        then(redisTemplate).should(times(3)).opsForHash();
        then(hashOperations).should().hasKey(email, TODAY_REMAIN_POSTS_COUNT);
        then(hashOperations).should().get(email, TODAY_REMAIN_POSTS_COUNT);
        then(hashOperations).should().put(email, TODAY_REMAIN_POSTS_COUNT, String.valueOf(todayRemainPostsCount - 1));
    }

    @Test
    public void validateLoginEmailState_호출_성공_hasKey_값_false() {
        //given
        given(redisTemplate.opsForHash()).willReturn(hashOperations);
        given(hashOperations.hasKey(email, "login")).willReturn(false);

        //when
        userSessionService.validateLoginEmailState(email);

        //then
        then(redisTemplate).should().opsForHash();
        then(hashOperations).should().hasKey(email, "login");
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateLoginEmailState_호출_실패_hasKey_값_true_에러처리() {
        //given
        given(redisTemplate.opsForHash()).willReturn(hashOperations);
        given(hashOperations.hasKey(email, "login")).willReturn(true);

        //when
        userSessionService.validateLoginEmailState(email);
    }

    @Test
    public void createLoginState_호출_성공() {
        //given
        given(redisTemplate.opsForHash()).willReturn(hashOperations);

        //when
        userSessionService.createLoginState(email);

        //then
        then(redisTemplate).should().opsForHash();
        then(hashOperations).should().put(email, "login", "true");
    }

    @Test
    public void deleteLoginState_호출_성공() {
        //given
        given(redisTemplate.opsForHash()).willReturn(hashOperations);

        //when
        userSessionService.deleteLoginState(email);

        //then
        then(redisTemplate).should().opsForHash();
        then(hashOperations).should().delete(email, "login");
    }

    @Test
    public void checkLastPostsSaveTime_호출_성공() {
        //given
        given(redisTemplate.opsForHash()).willReturn(hashOperations);
        given(hashOperations.hasKey(email, LAST_POSTS_SAVE_TIME)).willReturn(true);
        given(hashOperations.get(email, LAST_POSTS_SAVE_TIME)).willReturn(LocalDateTime.now().minusSeconds(POSTS_SAVE_INTERVAL_TIME));

        //when
        userSessionService.checkLastPostsSaveTime(email);

        //then
        then(redisTemplate).should(times(2)).opsForHash();
        then(hashOperations).should().hasKey(email, LAST_POSTS_SAVE_TIME);
        then(hashOperations).should().get(email, LAST_POSTS_SAVE_TIME);
    }

    @Test(expected = IllegalStateException.class)
    public void checkLastPostsSaveTime_호출_실패_POSTS_SAVE_INTERVAL_TIME_미만값_에러처리() {
        //given
        given(redisTemplate.opsForHash()).willReturn(hashOperations);
        given(hashOperations.hasKey(email, LAST_POSTS_SAVE_TIME)).willReturn(true);
        given(hashOperations.get(email, LAST_POSTS_SAVE_TIME)).willReturn(LocalDateTime.now().minusSeconds(POSTS_SAVE_INTERVAL_TIME - 1));

        //when
        userSessionService.checkLastPostsSaveTime(email);
    }
}
