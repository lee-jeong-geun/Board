package org.board.springboot.redis;

import org.assertj.core.api.Assertions;
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
    RedisTemplate redisTemplate;

    HashOperations<String, String, Object> hashOperations;

    @Before
    public void 세팅() {
        hashOperations = redisTemplate.opsForHash();
    }

    @Test
    public void 레디스_해시_삽입_성공() {
        //given
        hashOperations.put("jk@jk.com", "test", "true");

        //when
        Object result = hashOperations.get("jk@jk.com", "test");

        //then
        Assertions.assertThat(result).isEqualTo("true");
    }
}
