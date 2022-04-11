package org.board.springboot.redis;

import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTest {

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    HashOperations<String, String, Object> hashOperations;

    static final String key = "test";
    static final String hashKey = "test";
    static final String value = "true";

    @Before
    public void 세팅() {
        hashOperations = redisTemplate.opsForHash();
    }

    @After
    public void 초기화() {
        System.out.println("clear");
        hashOperations.delete(key, hashKey);
    }


    @Test
    public void 레디스_해시_삽입_성공() {
        //given
        hashOperations.put(key, hashKey, value);

        //when
        Object result = hashOperations.get(key, hashKey);

        //then
        Assertions.assertThat(result).isEqualTo(value);
    }

    @Test
    public void 레디스_해시_삭제_성공() {
        //given
        hashOperations.put(key, hashKey, value);

        //when
        Long result = hashOperations.delete(key, hashKey);

        //then
        Assertions.assertThat(result).isNotNull();
    }
}
