package org.board.springboot.auth;

import org.board.springboot.auth.domain.AuthUser;
import org.board.springboot.auth.domain.AuthUserRepository;
import org.board.springboot.user.domain.User;
import org.board.springboot.user.domain.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class AuthUserRepositoryTest {

    @Autowired
    AuthUserRepository authUserRepository;
    @Autowired
    UserRepository userRepository;

    @Test
    void authUser_저장_성공() {
        //given
        String name = "jk";
        String email = "jkjk";
        String password = "jkjk";
        User user = User.builder()
                .name(name)
                .email(email)
                .password(password)
                .build();
        LocalDateTime current = LocalDateTime.now();

        AuthUser authUser = AuthUser.builder()
                .lastLoggedIn(current)
                .user(user)
                .build();
        userRepository.save(user);
        authUserRepository.save(authUser);

        //when
        AuthUser result = authUserRepository.findByUserEmail(email).get();

        //then
        assertEquals(current, result.getLastLoggedIn());
        assertEquals(email, result.getUser().getEmail());
    }
}
