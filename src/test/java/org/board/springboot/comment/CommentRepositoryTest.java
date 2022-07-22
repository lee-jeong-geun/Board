package org.board.springboot.comment;

import org.board.springboot.comment.domain.Comment;
import org.board.springboot.comment.domain.CommentRepository;
import org.board.springboot.posts.domain.Posts;
import org.board.springboot.posts.domain.PostsRepository;
import org.board.springboot.user.domain.User;
import org.board.springboot.user.domain.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class CommentRepositoryTest {

    @Autowired
    CommentRepository commentRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PostsRepository postsRepository;

    @Test
    void comment_저장_성공() {
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
        assertEquals(1l, result.getId());
        assertEquals(content, result.getContent());
        assertEquals(user, result.getUser());
        assertEquals(posts, result.getPosts());
    }
}
