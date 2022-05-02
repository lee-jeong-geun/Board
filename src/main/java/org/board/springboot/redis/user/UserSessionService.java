package org.board.springboot.redis.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@RequiredArgsConstructor
@Service
public class UserSessionService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final int TODAY_POSTS_COUNT_MAX = 10;
    private static final int POSTS_SAVE_INTERVAL_TIME = 5;
    private static final String TODAY_REMAIN_POSTS_COUNT = "todayRemainPostsCount";
    private static final String LAST_POSTS_SAVE_TIME = "lastPostsSaveTime";
    private static final int LOGIN_SESSION_TIME = 30;

    public void checkTodayRemainPostsCount(String email) {
        if (!redisTemplate.opsForHash().hasKey(email, TODAY_REMAIN_POSTS_COUNT)) {
            return;
        }
        int remainPostsCount = Integer.parseInt(redisTemplate.opsForHash().get(email, TODAY_REMAIN_POSTS_COUNT).toString());
        if (remainPostsCount <= 0) {
            throw new IllegalStateException("오늘은 더이상 게시글을 올릴 수 없습니다.");
        }
    }

    public void checkLastPostsSaveTime(String email) {
        if (!redisTemplate.opsForHash().hasKey(email, LAST_POSTS_SAVE_TIME)) {
            return;
        }
        LocalDateTime lastSaveTime = LocalDateTime.parse(redisTemplate.opsForHash().get(email, LAST_POSTS_SAVE_TIME).toString());
        long intervalTime =  ChronoUnit.SECONDS.between(lastSaveTime, LocalDateTime.now());
        if (intervalTime < POSTS_SAVE_INTERVAL_TIME) {
            throw new IllegalStateException(String.format("게시글은 %d초 뒤에 작성 가능합니다.", POSTS_SAVE_INTERVAL_TIME - intervalTime));
        }
    }

    public void updateTodayRemainPostsCount(String email) {
        if (!redisTemplate.opsForHash().hasKey(email, TODAY_REMAIN_POSTS_COUNT)) {
            redisTemplate.opsForHash().put(email, TODAY_REMAIN_POSTS_COUNT, String.valueOf(TODAY_POSTS_COUNT_MAX));
        }
        int remainPostsCount = Integer.parseInt(redisTemplate.opsForHash().get(email, TODAY_REMAIN_POSTS_COUNT).toString()) - 1;
        redisTemplate.opsForHash().put(email, TODAY_REMAIN_POSTS_COUNT, String.valueOf(remainPostsCount));
    }

    public void updateLastPostsSaveTime(String email) {
        redisTemplate.opsForHash().put(email, LAST_POSTS_SAVE_TIME, String.valueOf(LocalDateTime.now()));
    }

    public void validateLoginEmailState(String email) {
        if (redisTemplate.opsForHash().hasKey(email, "login")) {
            throw new IllegalArgumentException("해당 아이디는 다른곳에서 로그인 중입니다.");
        }
    }

    public void createLoginState(String email) {
        LocalDateTime now = LocalDateTime.now();
        redisTemplate.opsForHash().put(email, "login", String.valueOf(now.plusMinutes(LOGIN_SESSION_TIME)));
    }

    public void deleteLoginState(String email) {
        redisTemplate.opsForHash().delete(email, "login");
    }
}
