package org.board.springboot.posts;

import org.board.springboot.posts.domain.Posts;
import org.board.springboot.posts.domain.PostsRepository;
import org.board.springboot.user.domain.User;
import org.board.springboot.user.domain.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.BDDAssertions.then;

@DataJpaTest
@RunWith(SpringRunner.class)
public class PostsRepositoryTest {

    @Autowired
    private PostsRepository postsRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void 포스트_저장_성공() {
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
        then(result.getTitle()).isEqualTo(title);
        then(result.getContent()).isEqualTo(content);
        then(result.getViewCount()).isEqualTo(viewCount);
        then(result.getUser().getName()).isEqualTo(name);
        then(result.getUser().getEmail()).isEqualTo(email);
    }

    @Test
    public void findByIdForUpdate_호출_성공() {
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
        then(result.getTitle()).isEqualTo(title);
        then(result.getContent()).isEqualTo(content);
        then(result.getViewCount()).isEqualTo(viewCount);
        then(result.getUser().getName()).isEqualTo(name);
        then(result.getUser().getEmail()).isEqualTo(email);
    }
}
