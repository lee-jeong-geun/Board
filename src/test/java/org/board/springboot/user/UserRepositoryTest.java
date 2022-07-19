package org.board.springboot.user;

import org.board.springboot.user.domain.User;
import org.board.springboot.user.domain.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @AfterEach
    public void clean() {
        userRepository.deleteAll();
    }

    final String name = "jk";
    final String email = "jk@jk.com";
    final String password = "jkjk";

    @Test
    void 유저저장_성공() {
        //given

        //when
        userRepository.save(User.builder()
                .name(name)
                .email(email)
                .password(password)
                .build());

        //then
        User user = userRepository.findAll().get(0);
        assertEquals(name, user.getName());
        assertEquals(email, user.getEmail());
        assertEquals(password, user.getPassword());
    }

    @Test
    void 유저삭제_성공() {
        //given
        userRepository.save(User.builder()
                .name(name)
                .email(email)
                .password(password)
                .build());

        //when
        userRepository.delete(userRepository.findAll().get(0));

        //then
        assertEquals(0, userRepository.findAll().size());
    }

    @Test
    void 유저수정_성공() {
        //given
        String modifyName = "jk2";
        User user = userRepository.save(User.builder()
                .name(name)
                .email(email)
                .password(password)
                .build());

        //when
        user.update(modifyName, email, password);
        user = userRepository.findById(user.getId()).get();

        //then
        assertEquals(modifyName, user.getName());
    }

    @Test
    void 유저조회_이메일_비밀번호_성공() {
        //given
        userRepository.save(User.builder()
                .name(name)
                .email(email)
                .password(password)
                .build());

        //when
        User user = userRepository.findByEmailAndPassword(email, password).get();

        //then
        assertEquals(email, user.getEmail());
        assertEquals(password, user.getPassword());
    }

    @Test
    void 유저조회_이메일_성공() {
        //given
        userRepository.save(User.builder()
                .name(name)
                .email(email)
                .password(password)
                .build());

        //when
        User user = userRepository.findByEmail(email).get();

        //then
        assertEquals(email, user.getEmail());
    }

    @Test
    void 유저_로그인_시간_업데이트_성공() {
        //given
        LocalDateTime current = LocalDateTime.now();
        User user = User.builder()
                .name(name)
                .email(email)
                .password(password)
                .build();

        userRepository.save(user);
        user.updateLastLogIn(current);

        //when
        User result = userRepository.findByEmail(email).get();

        //then
        assertEquals(current, result.getLastLogIn());
    }
}
