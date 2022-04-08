package org.board.springboot.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserSessionService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final int TODAY_POSTS_COUNT_MAX = 10;
    private static final String TODAY_REMAIN_POSTS_COUNT = "todayRemainPostsCount";

    public void checkTodayRemainPostsCount(String email) {
        if (!redisTemplate.opsForHash().hasKey(email, TODAY_REMAIN_POSTS_COUNT)) {
            return;
        }
        int remainPostsCount = Integer.parseInt(redisTemplate.opsForHash().get(email, TODAY_REMAIN_POSTS_COUNT).toString());
        if (remainPostsCount <= 0) {
            throw new IllegalStateException("오늘은 더이상 게시글을 올릴 수 없습니다.");
        }
    }

    public void updateTodayRemainPostsCount(String email) {
        if (!redisTemplate.opsForHash().hasKey(email, TODAY_REMAIN_POSTS_COUNT)) {
            redisTemplate.opsForHash().put(email, TODAY_REMAIN_POSTS_COUNT, String.valueOf(TODAY_POSTS_COUNT_MAX));
        }
        int remainPostsCount = Integer.parseInt(redisTemplate.opsForHash().get(email, TODAY_REMAIN_POSTS_COUNT).toString()) - 1;
        redisTemplate.opsForHash().put(email, TODAY_REMAIN_POSTS_COUNT, String.valueOf(remainPostsCount));
    }
}
