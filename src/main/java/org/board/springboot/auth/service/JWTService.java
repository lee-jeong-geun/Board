package org.board.springboot.auth.service;

import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@RequiredArgsConstructor
@Service
public class JWTService {

    private final Key key;

    public String createJWT(String email) {
        Date now = new Date();

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + 1000 * 60 * 30))
                .signWith(key)
                .compact();
    }

    public boolean validateJWT(String jwt) {
        Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt).getBody();
        return true;
    }

    public String getEmail(String jwt) {
        return Jwts.parserBuilder()
                .setSigningKey(key).build()
                .parseClaimsJws(jwt)
                .getBody()
                .getSubject();
    }
}
