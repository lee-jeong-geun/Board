package org.board.springboot.redis;

import org.board.springboot.redis.user.UserSessionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class UserSessionServiceTest {

    @Mock
    HashOperations<String, Object, Object> hashOperations;

    @Mock
    RedisTemplate<String, Object> redisTemplate;

    @Mock
    Clock clock;

    @InjectMocks
    UserSessionService userSessionService;

    final int TODAY_POSTS_COUNT_MAX = 10;
    final int POSTS_SAVE_INTERVAL_TIME = 5;
    final String TODAY_REMAIN_POSTS_COUNT = "todayRemainPostsCount";
    final String TODAY_REMAIN_POSTS_COUNT_LAST_UPDATE_DATE = "todayRemainPostsCountLastUpdateDate";
    final String LAST_POSTS_SAVE_TIME = "lastPostsSaveTime";
    final String email = "jk@jk.com";
    final LocalDateTime NOW = LocalDateTime.of(1993, 4, 11, 0, 0, 0);
    final Clock fixedClock = Clock.fixed(NOW.atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());

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

    @Test
    public void checkTodayRemainPostsCount_호출_실패_에러처리() {
        //given
        given(redisTemplate.opsForHash()).willReturn(hashOperations);
        given(hashOperations.hasKey(email, TODAY_REMAIN_POSTS_COUNT)).willReturn(true);
        given(hashOperations.get(email, TODAY_REMAIN_POSTS_COUNT)).willReturn(0);

        //when
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> userSessionService.checkTodayRemainPostsCount(email));

        //then
        assertEquals("오늘은 더이상 게시글을 올릴 수 없습니다.", exception.getMessage());
        then(redisTemplate).should(times(2)).opsForHash();
        then(hashOperations).should().hasKey(email, TODAY_REMAIN_POSTS_COUNT);
        then(hashOperations).should().get(email, TODAY_REMAIN_POSTS_COUNT);
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
        LocalDateTime expiredTime = NOW.minusMinutes(30);
        given(redisTemplate.opsForHash()).willReturn(hashOperations);
        given(hashOperations.hasKey(email, "login")).willReturn(true);
        given(hashOperations.get(email, "login")).willReturn(expiredTime);
        given(clock.instant()).willReturn(Instant.now(fixedClock));
        given(clock.getZone()).willReturn(fixedClock.getZone());

        //when
        userSessionService.validateLoginEmailState(email);

        //then
        then(redisTemplate).should(times(2)).opsForHash();
        then(hashOperations).should().hasKey(email, "login");
        then(hashOperations).should().get(email, "login");
        then(clock).should().instant();
        then(clock).should().getZone();
    }

    @Test
    public void validateLoginEmailState_호출_실패_hasKey_값_true_isBefore_true_값_에러처리() {
        //given
        LocalDateTime expiredTime = NOW.plusMinutes(30);
        given(redisTemplate.opsForHash()).willReturn(hashOperations);
        given(hashOperations.hasKey(email, "login")).willReturn(true);
        given(hashOperations.get(email, "login")).willReturn(expiredTime);
        given(clock.instant()).willReturn(Instant.now(fixedClock));
        given(clock.getZone()).willReturn(fixedClock.getZone());

        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userSessionService.validateLoginEmailState(email));

        //then
        assertEquals("해당 아이디는 다른곳에서 로그인 중입니다.", exception.getMessage());
        then(redisTemplate).should(times(2)).opsForHash();
        then(hashOperations).should().hasKey(email, "login");
        then(hashOperations).should().get(email, "login");
        then(clock).should().instant();
        then(clock).should().getZone();
    }

    @Test
    public void createLoginState_호출_성공() {
        //given
        int loginSessionTime = 30;
        given(redisTemplate.opsForHash()).willReturn(hashOperations);
        given(clock.instant()).willReturn(Instant.now(fixedClock));
        given(clock.getZone()).willReturn(fixedClock.getZone());

        //when
        userSessionService.createLoginState(email);

        //then
        then(redisTemplate).should().opsForHash();
        then(hashOperations).should().put(email, "login", String.valueOf(NOW.plusMinutes(loginSessionTime)));
        then(clock).should().instant();
        then(clock).should().getZone();
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
        given(hashOperations.get(email, LAST_POSTS_SAVE_TIME)).willReturn(NOW.minusSeconds(POSTS_SAVE_INTERVAL_TIME));
        given(clock.instant()).willReturn(Instant.now(fixedClock));
        given(clock.getZone()).willReturn(fixedClock.getZone());

        //when
        userSessionService.checkLastPostsSaveTime(email);

        //then
        then(redisTemplate).should(times(2)).opsForHash();
        then(hashOperations).should().hasKey(email, LAST_POSTS_SAVE_TIME);
        then(hashOperations).should().get(email, LAST_POSTS_SAVE_TIME);
        then(clock).should().instant();
        then(clock).should().getZone();
    }

    @Test
    public void checkLastPostsSaveTime_호출_실패_POSTS_SAVE_INTERVAL_TIME_미만값_에러처리() {
        //given
        int minusSecond = 1;
        given(redisTemplate.opsForHash()).willReturn(hashOperations);
        given(hashOperations.hasKey(email, LAST_POSTS_SAVE_TIME)).willReturn(true);
        given(hashOperations.get(email, LAST_POSTS_SAVE_TIME)).willReturn(NOW.minusSeconds(POSTS_SAVE_INTERVAL_TIME - minusSecond));
        given(clock.instant()).willReturn(Instant.now(fixedClock));
        given(clock.getZone()).willReturn(fixedClock.getZone());

        //when
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> userSessionService.checkLastPostsSaveTime(email));

        //then
        assertEquals(String.format("게시글은 %d초 뒤에 작성 가능합니다.", minusSecond), exception.getMessage());
        then(redisTemplate).should(times(2)).opsForHash();
        then(hashOperations).should().hasKey(email, LAST_POSTS_SAVE_TIME);
        then(hashOperations).should().get(email, LAST_POSTS_SAVE_TIME);
        then(clock).should().instant();
        then(clock).should().getZone();
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
        given(redisTemplate.opsForHash()).willReturn(hashOperations);
        given(clock.instant()).willReturn(Instant.now(fixedClock));
        given(clock.getZone()).willReturn(fixedClock.getZone());

        //when
        userSessionService.updateLastPostsSaveTime(email);

        //then
        then(redisTemplate).should().opsForHash();
        then(hashOperations).should().put(email, LAST_POSTS_SAVE_TIME, String.valueOf(NOW));
        then(clock).should().instant();
        then(clock).should().getZone();
    }

    @Test
    public void checkTodayRemainPostsCountUpdate_호출_성공_키_값_없는_상태() {
        //given
        given(redisTemplate.opsForHash()).willReturn(hashOperations);
        given(hashOperations.hasKey(email, TODAY_REMAIN_POSTS_COUNT_LAST_UPDATE_DATE)).willReturn(false);
        given(hashOperations.get(email, TODAY_REMAIN_POSTS_COUNT_LAST_UPDATE_DATE)).willReturn(NOW.toLocalDate());
        given(clock.instant()).willReturn(Instant.now(fixedClock));
        given(clock.getZone()).willReturn(fixedClock.getZone());

        //when
        userSessionService.checkTodayRemainPostsCountUpdate(email);

        //then
        then(redisTemplate).should(times(3)).opsForHash();
        then(hashOperations).should().hasKey(email, TODAY_REMAIN_POSTS_COUNT_LAST_UPDATE_DATE);
        then(hashOperations).should().put(email, TODAY_REMAIN_POSTS_COUNT_LAST_UPDATE_DATE, String.valueOf(NOW.toLocalDate()));
        then(hashOperations).should().get(email, TODAY_REMAIN_POSTS_COUNT_LAST_UPDATE_DATE);
        then(clock).should(times(2)).instant();
        then(clock).should(times(2)).getZone();
    }

    @Test
    public void checkTodayRemainPostsCountUpdate_호출_성공_키_값_존재_갱신() {
        //given
        LocalDate lastUpdateDate = NOW.toLocalDate().minusDays(1);

        given(redisTemplate.opsForHash()).willReturn(hashOperations);
        given(hashOperations.hasKey(email, TODAY_REMAIN_POSTS_COUNT_LAST_UPDATE_DATE)).willReturn(true);
        given(hashOperations.get(email, TODAY_REMAIN_POSTS_COUNT_LAST_UPDATE_DATE)).willReturn(lastUpdateDate);
        given(clock.instant()).willReturn(Instant.now(fixedClock));
        given(clock.getZone()).willReturn(fixedClock.getZone());

        //when
        userSessionService.checkTodayRemainPostsCountUpdate(email);

        //then
        then(redisTemplate).should(times(4)).opsForHash();
        then(hashOperations).should().hasKey(email, TODAY_REMAIN_POSTS_COUNT_LAST_UPDATE_DATE);
        then(hashOperations).should().get(email, TODAY_REMAIN_POSTS_COUNT_LAST_UPDATE_DATE);
        then(hashOperations).should().put(email, TODAY_REMAIN_POSTS_COUNT_LAST_UPDATE_DATE, String.valueOf(NOW.toLocalDate()));
        then(hashOperations).should().put(email, TODAY_REMAIN_POSTS_COUNT, String.valueOf(TODAY_POSTS_COUNT_MAX));
        then(clock).should(times(2)).instant();
        then(clock).should(times(2)).getZone();
    }

    @Test
    public void checkTodayRemainPostsCountUpdate_호출_성공_키_값_존재_미갱신() {
        //given
        LocalDate lastUpdateDate = NOW.toLocalDate();

        given(redisTemplate.opsForHash()).willReturn(hashOperations);
        given(hashOperations.hasKey(email, TODAY_REMAIN_POSTS_COUNT_LAST_UPDATE_DATE)).willReturn(true);
        given(hashOperations.get(email, TODAY_REMAIN_POSTS_COUNT_LAST_UPDATE_DATE)).willReturn(lastUpdateDate);
        given(clock.instant()).willReturn(Instant.now(fixedClock));
        given(clock.getZone()).willReturn(fixedClock.getZone());

        //when
        userSessionService.checkTodayRemainPostsCountUpdate(email);

        //then
        then(redisTemplate).should(times(2)).opsForHash();
        then(hashOperations).should().hasKey(email, TODAY_REMAIN_POSTS_COUNT_LAST_UPDATE_DATE);
        then(hashOperations).should().get(email, TODAY_REMAIN_POSTS_COUNT_LAST_UPDATE_DATE);
        then(clock).should().instant();
        then(clock).should().getZone();
    }
}
