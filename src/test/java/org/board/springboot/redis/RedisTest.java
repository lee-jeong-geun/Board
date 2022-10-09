package org.board.springboot.redis;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class RedisTest {

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    HashOperations<String, String, Object> hashOperations;
    ValueOperations<String, Object> valueOperations;

    String hashOperationsKey = "test";
    String hashTestKey = "test";
    String hashTestValue = "true";

    @BeforeEach
    public void 세팅() {
        hashOperations = redisTemplate.opsForHash();
        valueOperations = redisTemplate.opsForValue();
    }

    @AfterEach
    public void 초기화() {
        hashOperations.delete(hashOperationsKey, hashTestKey);
    }


    @Test
    void 레디스_해시_삽입_성공() {
        //given
        hashOperations.put(hashOperationsKey, hashTestKey, hashTestValue);

        //when
        Object result = hashOperations.get(hashOperationsKey, hashTestKey);

        //then
        assertEquals(hashTestValue, result);
    }

    @Test
    void 레디스_해시_삭제_성공() {
        //given
        hashOperations.put(hashOperationsKey, hashTestKey, hashTestValue);

        //when
        Long result = hashOperations.delete(hashOperationsKey, hashTestKey);

        //then
        assertNotNull(result);
    }

    @Test
    void 레디스_incr_성공() {
        //given
        String key = "incrTest";
        Object value = "0";
        valueOperations.set(key, value);

        //when
        valueOperations.increment(key, 1);
        Object result = valueOperations.get(key);

        //then
        assertEquals("1", result);
    }
}
