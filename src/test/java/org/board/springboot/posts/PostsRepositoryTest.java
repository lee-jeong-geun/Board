package org.board.springboot.posts;

import org.board.springboot.posts.domain.Posts;
import org.board.springboot.posts.domain.PostsRepository;
import org.board.springboot.user.domain.User;
import org.board.springboot.user.domain.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class PostsRepositoryTest {

    @Autowired
    PostsRepository postsRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    void 포스트_저장_성공() {
        //given
        String name = "jk";
        String email = "jk@jk.com";
        String password = "jkjk";
        User user = User.builder()
                .name(name)
                .email(email)
                .password(password)
                .build();

        String title = "title";
        String content = "content";
        int viewCount = 0;
        Posts posts = Posts.builder()
                .title(title)
                .content(content)
                .viewCount(viewCount)
                .user(user)
                .build();
        userRepository.save(user);

        //when
        Posts result = postsRepository.save(posts);

        //then
        assertEquals(title, result.getTitle());
        assertEquals(content, result.getContent());
        assertEquals(viewCount, result.getViewCount());
        assertEquals(name, result.getUser().getName());
        assertEquals(email, result.getUser().getEmail());
    }

    @Test
    void findByIdForUpdate_호출_성공() {
        //given
        String name = "jk";
        String email = "jk@jk.com";
        String password = "jkjk";
        User user = User.builder()
                .name(name)
                .email(email)
                .password(password)
                .build();

        String title = "title";
        String content = "content";
        int viewCount = 0;
        Posts posts = Posts.builder()
                .title(title)
                .content(content)
                .viewCount(viewCount)
                .user(user)
                .build();
        userRepository.save(user);
        postsRepository.save(posts);

        //when
        Posts result = postsRepository.findByIdForUpdate(posts.getId()).get();

        //then
        assertEquals(title, result.getTitle());
        assertEquals(content, result.getContent());
        assertEquals(viewCount, result.getViewCount());
        assertEquals(name, result.getUser().getName());
        assertEquals(email, result.getUser().getEmail());
    }
}
