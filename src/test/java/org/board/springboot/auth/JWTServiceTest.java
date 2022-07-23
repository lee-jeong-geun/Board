package org.board.springboot.auth;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.board.springboot.auth.service.JWTService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.Key;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class JWTServiceTest {

    @Autowired
    JWTService jwtService;

    @Autowired
    Key key;

    String email = "jk@jk.com";

    @Test
    void createJWT_호출_값_검증_성공() {
        //given
        JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(key).build();

        //when
        String jwt = jwtService.createJWT(email);
        Date now = jwtParser.parseClaimsJws(jwt).getBody().getIssuedAt();

        //then
        assertEquals(email, jwtParser.parseClaimsJws(jwt).getBody().getSubject());
        assertEquals(new Date().after(now), true);
        assertEquals(now.getTime() + 1000 * 60 * 30, jwtParser.parseClaimsJws(jwt).getBody().getExpiration().getTime());
    }

    @Test
    void validateJWT_호출_성공() {
        //given
        String jwt = jwtService.createJWT(email);

        //when
        boolean result = jwtService.validateJWT(jwt);

        //then
        assertEquals(true, result);
    }

    @Test
    void getEmail_호출_성공() {
        //given
        String jwt = jwtService.createJWT(email);

        //when
        String resultEmail = jwtService.getEmail(jwt);

        //then
        assertEquals(email, resultEmail);
    }
}
