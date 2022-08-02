package org.board.springboot.comment;

import org.board.springboot.comment.domain.Comment;
import org.board.springboot.comment.domain.CommentRepository;
import org.board.springboot.comment.dto.CommentDeleteRequestDto;
import org.board.springboot.comment.dto.CommentFindResponseDto;
import org.board.springboot.comment.dto.CommentSaveRequestDto;
import org.board.springboot.comment.service.CommentService;
import org.board.springboot.posts.domain.Posts;
import org.board.springboot.posts.domain.PostsRepository;
import org.board.springboot.user.domain.User;
import org.board.springboot.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @InjectMocks
    CommentService commentService;
    @Mock
    CommentRepository commentRepository;
    @Mock
    UserService userService;
    @Mock
    PostsRepository postsRepository;

    final String content = "content";
    final String userEmail = "jk@jk.com";

    @Test
    void save_호출_성공() throws Exception {
        //given
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
        assertEquals(1l, result);
    }

    @Test
    void save_호출_실패_user_에러처리() {
        //given
        Long postsId = 1l;

        CommentSaveRequestDto commentSaveRequestDto = CommentSaveRequestDto.builder()
                .content(content)
                .userEmail(userEmail)
                .postsId(postsId)
                .build();
        given(userService.findByEmail(userEmail)).willThrow(new IllegalArgumentException("해당 유저가 없습니다."));

        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> commentService.save(commentSaveRequestDto));

        //then
        assertEquals("해당 유저가 없습니다.", exception.getMessage());
    }

    @Test
    void save_호출_실패_posts_에러처리() {
        //given
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
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> commentService.save(commentSaveRequestDto));

        //then
        assertEquals("해당 게시글이 없습니다.", exception.getMessage());
    }

    @Test
    void findByPostsId_호출_성공() throws Exception {
        //given
        User user = User.builder()
                .email(userEmail)
                .build();
        Posts posts = Posts.builder()
                .user(user)
                .build();
        Comment comment = Comment.builder()
                .content(content)
                .user(user)
                .posts(posts)
                .build();
        Long commentId = 1L;
        Long postsId = 1L;
        Field field = comment.getClass().getDeclaredField("id");
        field.setAccessible(true);
        field.set(comment, commentId);

        given(postsRepository.findById(postsId)).willReturn(Optional.of(posts));

        //when
        List<CommentFindResponseDto> commentList = commentService.findByPostsId(postsId);

        //then
        then(postsRepository).should().findById(postsId);
        assertEquals(1, commentList.size());
        assertEquals(comment.getId(), commentList.get(0).getCommentId());
        assertEquals(comment.getContent(), commentList.get(0).getContent());
        assertEquals(comment.getUser().getEmail(), commentList.get(0).getUserEmail());
    }

    @Test
    void findByPostsId_호출_실패_게시글_조회_실패_에러처리() {
        //given
        Long postsId = 1l;

        given(postsRepository.findById(postsId)).willThrow(new IllegalStateException("해당 게시글이 없습니다."));

        //when
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> commentService.findByPostsId(postsId));

        //then
        assertEquals("해당 게시글이 없습니다.", exception.getMessage());
    }

    @Test
    void deleteById_호출_성공() {
        //given
        Long commentId = 1L;
        User user = User.builder()
                .email(userEmail)
                .build();
        Posts posts = Posts.builder()
                .user(user)
                .build();
        Comment comment = Comment.builder()
                .user(user)
                .posts(posts)
                .build();
        CommentDeleteRequestDto commentDeleteRequestDto = CommentDeleteRequestDto.builder()
                .commentId(commentId)
                .userEmail(userEmail)
                .build();

        given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));

        //when
        Long result = commentService.deleteById(commentDeleteRequestDto);

        //then
        then(commentRepository).should().findById(commentId);
        then(commentRepository).should().delete(comment);
        assertEquals(commentId, result);
    }

    @Test
    void deleteById_호출_실패_조회_실패_에러처리() {
        //given
        Long commentId = 1L;
        CommentDeleteRequestDto commentDeleteRequestDto = CommentDeleteRequestDto.builder()
                .commentId(commentId)
                .userEmail(userEmail)
                .build();

        given(commentRepository.findById(commentId)).willThrow(new IllegalStateException("해당 댓글이 없습니다."));

        //when
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> commentService.deleteById(commentDeleteRequestDto));

        //then
        assertEquals("해당 댓글이 없습니다.", exception.getMessage());
    }

    @Test
    void deleteById_호출_실패_타_작성자_에러처리() {
        //given
        Long commentId = 1L;
        String inValidEmail = "invalidEmail";
        User user = User.builder()
                .email(userEmail)
                .build();
        Posts posts = Posts.builder()
                .user(user)
                .build();
        Comment comment = Comment.builder()
                .user(user)
                .posts(posts)
                .build();

        CommentDeleteRequestDto commentDeleteRequestDto = CommentDeleteRequestDto.builder()
                .commentId(commentId)
                .userEmail(inValidEmail)
                .build();

        given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));

        //when
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> commentService.deleteById(commentDeleteRequestDto));

        //then
        assertEquals("해당 댓글의 작성자가 아닙니다.", exception.getMessage());
    }
}
