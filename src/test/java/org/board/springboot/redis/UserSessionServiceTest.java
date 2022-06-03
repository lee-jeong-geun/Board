package org.board.springboot.redis;

import org.board.springboot.redis.user.UserSessionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@RunWith(PowerMockRunner.class)
@PrepareForTest({UserSessionService.class})
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
    private final String TODAY_REMAIN_POSTS_COUNT_LAST_UPDATE_DATE = "todayRemainPostsCountLastUpdateDate";
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

    @Test
    public void validateLoginEmailState_호출_성공_hasKey_값_null() {
        //given
        given(redisTemplate.opsForHash()).willReturn(hashOperations);
        given(hashOperations.hasKey(email, "login")).willReturn(null);

        //when
        userSessionService.validateLoginEmailState(email);

        //then
        then(redisTemplate).should().opsForHash();
        then(hashOperations).should().hasKey(email, "login");
    }

    @Test
    public void validateLoginEmailState_호출_성공_hasKey_값_true_isBefore_false_값() {
        //given
        LocalDateTime current = LocalDateTime.now();
        LocalDateTime expiredTime = current.minusMinutes(30);
        PowerMockito.mockStatic(LocalDateTime.class);
        given(LocalDateTime.now()).willReturn(current);
        given(LocalDateTime.parse(expiredTime.toString())).willReturn(expiredTime);
        given(redisTemplate.opsForHash()).willReturn(hashOperations);
        given(hashOperations.hasKey(email, "login")).willReturn(true);
        given(hashOperations.get(email, "login")).willReturn(expiredTime);

        //when
        userSessionService.validateLoginEmailState(email);

        //then
        then(redisTemplate).should(times(2)).opsForHash();
        then(hashOperations).should().hasKey(email, "login");
        then(hashOperations).should().get(email, "login");
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateLoginEmailState_호출_실패_hasKey_값_true_isBefore_true_값_에러처리() {
        //given
        LocalDateTime current = LocalDateTime.now();
        LocalDateTime expiredTime = current.plusMinutes(30);
        PowerMockito.mockStatic(LocalDateTime.class);
        given(LocalDateTime.now()).willReturn(current);
        given(LocalDateTime.parse(expiredTime.toString())).willReturn(expiredTime);
        given(redisTemplate.opsForHash()).willReturn(hashOperations);
        given(hashOperations.hasKey(email, "login")).willReturn(true);
        given(hashOperations.get(email, "login")).willReturn(expiredTime);

        //when
        userSessionService.validateLoginEmailState(email);
    }

    @Test
    public void createLoginState_호출_성공() {
        //given
        int loginSessionTime = 30;
        LocalDateTime current = LocalDateTime.now();
        PowerMockito.mockStatic(LocalDateTime.class);
        given(LocalDateTime.now()).willReturn(current);
        given(redisTemplate.opsForHash()).willReturn(hashOperations);

        //when
        userSessionService.createLoginState(email);

        //then
        then(redisTemplate).should().opsForHash();
        then(hashOperations).should().put(email, "login", String.valueOf(current.plusMinutes(loginSessionTime)));
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

    @Test
    public void checkLastPostsSaveTime_호출_성공_hasKey_값_false() {
        //given
        given(redisTemplate.opsForHash()).willReturn(hashOperations);
        given(hashOperations.hasKey(email, LAST_POSTS_SAVE_TIME)).willReturn(false);

        //when
        userSessionService.checkLastPostsSaveTime(email);

        //then
        then(redisTemplate).should().opsForHash();
        then(hashOperations).should().hasKey(email, LAST_POSTS_SAVE_TIME);
    }

    @Test
    public void updateLastPostsSaveTime_호출_성공() {
        //given
        LocalDateTime current = LocalDateTime.now();
        PowerMockito.mockStatic(LocalDateTime.class);
        given(redisTemplate.opsForHash()).willReturn(hashOperations);
        given(LocalDateTime.now()).willReturn(current);

        //when
        userSessionService.updateLastPostsSaveTime(email);

        //then
        then(redisTemplate).should().opsForHash();
        then(hashOperations).should().put(email, LAST_POSTS_SAVE_TIME, String.valueOf(current));
    }

    @Test
    public void checkTodayRemainPostsCountUpdate_호출_성공_키_값_없는_상태() {
        //given
        LocalDate current = LocalDate.now();
        PowerMockito.mockStatic(LocalDate.class);

        given(redisTemplate.opsForHash()).willReturn(hashOperations);
        given(LocalDate.now()).willReturn(current);
        given(hashOperations.hasKey(email, TODAY_REMAIN_POSTS_COUNT_LAST_UPDATE_DATE)).willReturn(false);
        given(hashOperations.get(email, TODAY_REMAIN_POSTS_COUNT_LAST_UPDATE_DATE)).willReturn(current);

        //when
        userSessionService.checkTodayRemainPostsCountUpdate(email);

        //then
        then(redisTemplate).should(times(3)).opsForHash();
        then(hashOperations).should().hasKey(email, TODAY_REMAIN_POSTS_COUNT_LAST_UPDATE_DATE);
        then(hashOperations).should().put(email, TODAY_REMAIN_POSTS_COUNT_LAST_UPDATE_DATE, String.valueOf(current));
        then(hashOperations).should().get(email, TODAY_REMAIN_POSTS_COUNT_LAST_UPDATE_DATE);
    }

    @Test
    public void checkTodayRemainPostsCountUpdate_호출_성공_키_값_존재_갱신() {
        //given
        LocalDate lastUpdateDate = LocalDate.now().minusDays(1);
        LocalDate current = LocalDate.now();
        PowerMockito.mockStatic(LocalDate.class);

        given(redisTemplate.opsForHash()).willReturn(hashOperations);
        given(LocalDate.now()).willReturn(current);
        given(hashOperations.hasKey(email, TODAY_REMAIN_POSTS_COUNT_LAST_UPDATE_DATE)).willReturn(true);
        given(hashOperations.get(email, TODAY_REMAIN_POSTS_COUNT_LAST_UPDATE_DATE)).willReturn(lastUpdateDate);
        given(LocalDate.parse(lastUpdateDate.toString())).willReturn(lastUpdateDate);

        //when
        userSessionService.checkTodayRemainPostsCountUpdate(email);

        //then
        then(redisTemplate).should(times(4)).opsForHash();
        then(hashOperations).should().hasKey(email, TODAY_REMAIN_POSTS_COUNT_LAST_UPDATE_DATE);
        then(hashOperations).should().get(email, TODAY_REMAIN_POSTS_COUNT_LAST_UPDATE_DATE);
        then(hashOperations).should().put(email, TODAY_REMAIN_POSTS_COUNT_LAST_UPDATE_DATE, String.valueOf(LocalDate.now()));
        then(hashOperations).should().put(email, TODAY_REMAIN_POSTS_COUNT, String.valueOf(TODAY_POSTS_COUNT_MAX));
    }

    @Test
    public void checkTodayRemainPostsCountUpdate_호출_성공_키_값_존재_미갱신() {
        //given
        LocalDate lastUpdateDate = LocalDate.now();
        LocalDate current = LocalDate.now();
        PowerMockito.mockStatic(LocalDate.class);

        given(redisTemplate.opsForHash()).willReturn(hashOperations);
        given(LocalDate.now()).willReturn(current);
        given(hashOperations.hasKey(email, TODAY_REMAIN_POSTS_COUNT_LAST_UPDATE_DATE)).willReturn(true);
        given(hashOperations.get(email, TODAY_REMAIN_POSTS_COUNT_LAST_UPDATE_DATE)).willReturn(lastUpdateDate);
        given(LocalDate.parse(lastUpdateDate.toString())).willReturn(lastUpdateDate);

        //when
        userSessionService.checkTodayRemainPostsCountUpdate(email);

        //then
        then(redisTemplate).should(times(2)).opsForHash();
        then(hashOperations).should().hasKey(email, TODAY_REMAIN_POSTS_COUNT_LAST_UPDATE_DATE);
        then(hashOperations).should().get(email, TODAY_REMAIN_POSTS_COUNT_LAST_UPDATE_DATE);
    }
}
