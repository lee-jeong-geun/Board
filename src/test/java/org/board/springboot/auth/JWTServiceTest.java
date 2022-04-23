package org.board.springboot.auth;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.board.springboot.auth.service.JWTService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.Key;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)
public class JWTServiceTest {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private Key key;

    @Test
    public void createJWT_호출_값_검증_성공() {
        //given
        String email = "jk@jk.com";
        JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(key).build();

        //when
        String jwt = jwtService.createJWT(email);
        Date now = jwtParser.parseClaimsJws(jwt).getBody().getIssuedAt();

        //then
        assertThat(jwtParser.parseClaimsJws(jwt).getBody().getSubject()).isEqualTo(email);
        assertThat(now).isBefore(new Date());
        assertThat(jwtParser.parseClaimsJws(jwt).getBody().getExpiration().getTime()).isEqualTo(now.getTime() + 1000 * 60 * 30);
    }
}
