package org.board.springboot.comment;

import org.board.springboot.comment.domain.Comment;
import org.board.springboot.comment.domain.CommentRepository;
import org.board.springboot.posts.domain.Posts;
import org.board.springboot.posts.domain.PostsRepository;
import org.board.springboot.user.domain.User;
import org.board.springboot.user.domain.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@RunWith(SpringRunner.class)
public class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostsRepository postsRepository;

    @Test
    public void comment_저장_성공() {
        //given
        String name = "jk";
        String email = "jk@jk.com";
        String password = "jkjk";
        String title = "title";
        String content = "content";

        User user = User.builder()
                .name(name)
                .email(email)
                .password(password)
                .build();
        Posts posts = Posts.builder()
                .title(title)
                .content(content)
                .user(user)
                .build();
        Comment comment = Comment.builder()
                .content(content)
                .user(user)
                .posts(posts)
                .build();

        userRepository.save(user);
        postsRepository.save(posts);

        //when
        commentRepository.save(comment);
        Comment result = commentRepository.findById(1l).get();

        //then
        assertThat(result.getId()).isEqualTo(1l);
        assertThat(result.getContent()).isEqualTo(content);
        assertThat(result.getUser()).isEqualTo(user);
        assertThat(result.getPosts()).isEqualTo(posts);
    }
}
