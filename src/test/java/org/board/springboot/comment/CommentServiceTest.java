package org.board.springboot.comment;

import org.board.springboot.comment.domain.Comment;
import org.board.springboot.comment.domain.CommentRepository;
import org.board.springboot.comment.dto.CommentSaveRequestDto;
import org.board.springboot.comment.service.CommentService;
import org.board.springboot.posts.domain.Posts;
import org.board.springboot.posts.domain.PostsRepository;
import org.board.springboot.user.domain.User;
import org.board.springboot.user.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@RunWith(MockitoJUnitRunner.class)
public class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserService userService;
    @Mock
    private PostsRepository postsRepository;

    @Test
    public void save_호출_성공() throws Exception {
        //given
        String content = "content";
        String userEmail = "jk@jk.com";
        Long postsId = 1l;
        User user = User.builder().build();
        Posts posts = Posts.builder()
                .user(user)
                .build();
        Comment comment = Comment.builder()
                .content(content)
                .user(user)
                .posts(posts)
                .build();
        Field field = comment.getClass().getDeclaredField("id");
        field.setAccessible(true);
        field.set(comment, 1l);

        CommentSaveRequestDto commentSaveRequestDto = CommentSaveRequestDto.builder()
                .content(content)
                .userEmail(userEmail)
                .postsId(postsId)
                .build();
        given(userService.findByEmail(userEmail)).willReturn(user);
        given(postsRepository.findById(postsId)).willReturn(Optional.of(posts));
        given(commentRepository.save(any())).willReturn(comment);

        //when
        Long result = commentService.save(commentSaveRequestDto);

        //then
        then(userService).should().findByEmail(userEmail);
        then(postsRepository).should().findById(postsId);
        then(commentRepository).should().save(any());
        assertThat(result).isEqualTo(1l);
    }

    @Test(expected = IllegalArgumentException.class)
    public void save_호출_실패_user_에러처리() {
        //given
        String content = "content";
        String userEmail = "jk@jk.com";
        Long postsId = 1l;

        CommentSaveRequestDto commentSaveRequestDto = CommentSaveRequestDto.builder()
                .content(content)
                .userEmail(userEmail)
                .postsId(postsId)
                .build();
        given(userService.findByEmail(userEmail)).willThrow(new IllegalArgumentException("해당 유저가 없습니다."));

        //when
        commentService.save(commentSaveRequestDto);
    }

    @Test(expected = IllegalStateException.class)
    public void save_호출_실패_posts_에러처리() {
        //given
        String content = "content";
        String userEmail = "jk@jk.com";
        Long postsId = 1l;
        User user = User.builder().build();

        CommentSaveRequestDto commentSaveRequestDto = CommentSaveRequestDto.builder()
                .content(content)
                .userEmail(userEmail)
                .postsId(postsId)
                .build();
        given(userService.findByEmail(userEmail)).willReturn(user);
        given(postsRepository.findById(postsId)).willThrow(new IllegalStateException("해당 게시글이 없습니다."));

        //when
        commentService.save(commentSaveRequestDto);
    }
}
