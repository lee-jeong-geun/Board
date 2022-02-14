package org.board.springboot.user;

import org.board.springboot.user.domain.User;
import org.board.springboot.user.domain.UserRepository;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@RunWith(SpringRunner.class)
public class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @After
    public void clean() {
        userRepository.deleteAll();
    }


    @Test
    public void 유저저장_성공() {
        //given
        String name = "jk";
        String email = "jk@jk.com";
        String password = "jkjk";

        //when
        userRepository.save(User.builder()
                .name(name)
                .email(email)
                .password(password)
                .build());

        //then
        User user = userRepository.findAll().get(0);
        assertThat(user.getName()).isEqualTo(name);
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getPassword()).isEqualTo(password);
    }

    @Test
    public void 유저삭제_성공() {
        //given
        String name = "jk";
        String email = "jk@jk.com";
        String password = "jkjk";
        userRepository.save(User.builder()
                .name(name)
                .email(email)
                .password(password)
                .build());

        //when
        userRepository.delete(userRepository.findAll().get(0));

        //then
        assertThat(userRepository.findAll().size()).isEqualTo(0);
    }

    @Test
    public void 유저수정_성공() {
        //given
        String name = "jk";
        String email = "jk@jk.com";
        String password = "jkjk";
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
        assertThat(user.getName()).isEqualTo(modifyName);
    }

    @Test
    public void 유저조회_이메일_비밀번호_성공() {
        //given
        String name = "jk";
        String email = "jk@jk.com";
        String password = "jkjk";
        userRepository.save(User.builder()
                .name(name)
                .email(email)
                .password(password)
                .build());

        //when
        User user = userRepository.findByEmailAndPassword(email, password).get();

        //then
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getPassword()).isEqualTo(password);
    }
}
