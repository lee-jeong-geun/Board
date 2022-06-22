package org.board.springboot.auth;

import org.board.springboot.auth.domain.AuthUser;
import org.board.springboot.auth.domain.AuthUserRepository;
import org.board.springboot.user.domain.User;
import org.board.springboot.user.domain.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@RunWith(SpringRunner.class)
public class AuthUserRepositoryTest {

    @Autowired
    private AuthUserRepository authUserRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    void authUser_저장_성공() {
        //given
        String email = "jkjk";
        User user = User.builder()
                .email(email)
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
        assertThat(result.getLastLoggedIn()).isEqualTo(current);
        assertThat(result.getUser().getEmail()).isEqualTo(email);
    }
}
