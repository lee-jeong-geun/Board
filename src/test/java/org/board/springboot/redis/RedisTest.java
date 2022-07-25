package org.board.springboot.redis;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class RedisTest {

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    HashOperations<String, String, Object> hashOperations;

    String key = "test";
    String hashKey = "test";
    String value = "true";

    @BeforeEach
    public void 세팅() {
        hashOperations = redisTemplate.opsForHash();
    }

    @AfterEach
    public void 초기화() {
        System.out.println("clear");
        hashOperations.delete(key, hashKey);
    }


    @Test
    void 레디스_해시_삽입_성공() {
        //given
        hashOperations.put(key, hashKey, value);

        //when
        Object result = hashOperations.get(key, hashKey);

        //then
        assertEquals(value, result);
    }

    @Test
    void 레디스_해시_삭제_성공() {
        //given
        hashOperations.put(key, hashKey, value);

        //when
        Long result = hashOperations.delete(key, hashKey);

        //then
        assertNotNull(result);
    }
}
