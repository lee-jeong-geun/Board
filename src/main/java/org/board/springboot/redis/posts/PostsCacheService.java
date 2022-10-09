package org.board.springboot.redis.posts;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostsCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final String VIEW_COUNT = "postsViewCount";

    public boolean hasPostsViewCountKey(Long postsId) {
        String key = getPostsViewCount(postsId);
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void setPostsViewCount(Long postsId, int viewCount) {
        String key = getPostsViewCount(postsId);
        Object value = String.valueOf(viewCount);
        redisTemplate.opsForValue().set(key, value);
    }

    public int incrementPostsViewCount(Long postsId, int updateCount) {
        String key = getPostsViewCount(postsId);
        return redisTemplate.opsForValue().increment(key, updateCount).intValue();
    }

    private String getPostsViewCount(Long postsId) {
        return VIEW_COUNT + ":" + postsId;
    }
}
