package org.board.springboot.comment;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.board.springboot.auth.service.AuthService;
import org.board.springboot.auth.service.JWTService;
import org.board.springboot.comment.controller.CommentApiController;
import org.board.springboot.comment.dto.CommentDeleteRequestDto;
import org.board.springboot.comment.dto.CommentFindResponseDto;
import org.board.springboot.comment.dto.CommentSaveRequestBody;
import org.board.springboot.comment.dto.CommentSaveRequestDto;
import org.board.springboot.comment.service.CommentService;
import org.board.springboot.common.dto.ApiResponse;
import org.board.springboot.common.dto.ExceptionResponse;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockCookie;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentApiController.class)
public class CommentApiControllerTest {

    @MockBean
    CommentService commentService;
    @MockBean
    AuthService authService;
    @MockBean
    JWTService jwtService;
    @Mock
    MockCookie mockCookie;

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    final String content = "content";
    final String userEmail = "jk@jk.com";
    final Long postsId = 1l;
    final Long commentId = 1L;

    @Test
    void Comment_save_호출_성공() throws Exception {
        //given
        String url = "/api/v1/comment";
        String validToken = "valid";

        CommentSaveRequestBody commentSaveRequestBody = CommentSaveRequestBody.builder()
                .content(content)
                .postsId(postsId)
                .build();

        ApiResponse<Long> apiResponse = ApiResponse.<Long>builder()
                .success(true)
                .response(commentId)
                .build();
        ArgumentCaptor<CommentSaveRequestDto> argumentCaptor = ArgumentCaptor.forClass(CommentSaveRequestDto.class);
        given(authService.isLoggedIn()).willReturn(true);
        given(mockCookie.getName()).willReturn("token");
        given(mockCookie.getValue()).willReturn(validToken);
        given(jwtService.getEmail(validToken)).willReturn(userEmail);
        given(commentService.save(any())).willReturn(commentId);

        //when
        ResultActions resultActions = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentSaveRequestBody))
                .cookie(mockCookie));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(apiResponse)));
        then(authService).should().isLoggedIn();
        then(mockCookie).should(times(2)).getName();
        then(mockCookie).should(times(3)).getValue();
        then(jwtService).should().getEmail(validToken);
        then(commentService).should().save(argumentCaptor.capture());
        assertEquals(content, argumentCaptor.getValue().getContent());
        assertEquals(userEmail, argumentCaptor.getValue().getUserEmail());
        assertEquals(postsId, argumentCaptor.getValue().getPostsId());
    }

    @Test
    void Comment_save_호출_실패_잘못된_postsId_에러처리() throws Exception {
        //given
        String url = "/api/v1/comment";
        String validToken = "valid";

        CommentSaveRequestBody commentSaveRequestBody = CommentSaveRequestBody.builder()
                .content(content)
                .postsId(postsId)
                .build();
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .success(false)
                .message("해당 게시글이 없습니다.")
                .build();

        ArgumentCaptor<CommentSaveRequestDto> argumentCaptor = ArgumentCaptor.forClass(CommentSaveRequestDto.class);
        given(commentService.save(any())).willThrow(new IllegalStateException("해당 게시글이 없습니다."));
        given(authService.isLoggedIn()).willReturn(true);
        given(mockCookie.getName()).willReturn("token");
        given(mockCookie.getValue()).willReturn(validToken);
        given(jwtService.getEmail(validToken)).willReturn(userEmail);

        //when
        ResultActions resultActions = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentSaveRequestBody))
                .cookie(mockCookie));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(content().bytes(objectMapper.writeValueAsBytes(exceptionResponse)));
        then(authService).should().isLoggedIn();
        then(mockCookie).should(times(2)).getName();
        then(mockCookie).should(times(3)).getValue();
        then(jwtService).should().getEmail(validToken);
        then(commentService).should().save(argumentCaptor.capture());
        assertEquals(content, argumentCaptor.getValue().getContent());
        assertEquals(userEmail, argumentCaptor.getValue().getUserEmail());
        assertEquals(postsId, argumentCaptor.getValue().getPostsId());
    }

    @Test
    void Comment_save_호출_로그인_상태_에러처리() throws Exception {
        //given
        String url = "/api/v1/comment";

        CommentSaveRequestBody commentSaveRequestBody = CommentSaveRequestBody.builder()
                .content(content)
                .postsId(postsId)
                .build();
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .success(false)
                .message("로그인 상태가 아닙니다.")
                .build();

        given(authService.isLoggedIn()).willReturn(false);

        //when
        ResultActions resultActions = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentSaveRequestBody)));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(content().bytes(objectMapper.writeValueAsBytes(exceptionResponse)));
        then(authService).should().isLoggedIn();
    }

    @Test
    void getComments_호출_성공() throws Exception {
        //given
        String url = "/api/v1/comment/" + postsId;
        CommentFindResponseDto commentFindResponseDto = CommentFindResponseDto.builder()
                .commentId(commentId)
                .content(content)
                .userEmail(userEmail)
                .build();
        List<CommentFindResponseDto> commentList = new ArrayList<>();
        commentList.add(commentFindResponseDto);

        ApiResponse<List<CommentFindResponseDto>> apiResponse = ApiResponse.<List<CommentFindResponseDto>>builder()
                .success(true)
                .response(commentList)
                .build();

        given(commentService.findByPostsId(postsId)).willReturn(commentList);

        //when
        ResultActions resultActions = mockMvc.perform(get(url)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(apiResponse)));
        then(commentService).should().findByPostsId(postsId);
    }

    @Test
    void getComments_호출_실패_게시글_조회_실패_에러처리() throws Exception {
        //given
        String url = "/api/v1/comment/" + postsId;

        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .success(false)
                .message("해당 게시글이 없습니다.")
                .build();

        given(commentService.findByPostsId(postsId)).willThrow(new IllegalStateException("해당 게시글이 없습니다."));

        //when
        ResultActions resultActions = mockMvc.perform(get(url)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(content().bytes(objectMapper.writeValueAsBytes(exceptionResponse)));
    }

    @Test
    void deleteComment_호출_성공() throws Exception {
        //given
        String url = "/api/v1/comment/" + commentId;
        String validToken = "valid";

        ApiResponse<Long> apiResponse = ApiResponse.<Long>builder()
                .success(true)
                .response(commentId)
                .build();

        ArgumentCaptor<CommentDeleteRequestDto> argumentCaptor = ArgumentCaptor.forClass(CommentDeleteRequestDto.class);
        given(authService.isLoggedIn()).willReturn(true);
        given(mockCookie.getName()).willReturn("token");
        given(mockCookie.getValue()).willReturn(validToken);
        given(jwtService.getEmail(validToken)).willReturn(userEmail);
        given(commentService.deleteById(any())).willReturn(commentId);

        //when
        ResultActions resultActions = mockMvc.perform(delete(url)
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(mockCookie));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(apiResponse)));
        then(authService).should().isLoggedIn();
        then(mockCookie).should(times(2)).getName();
        then(mockCookie).should(times(3)).getValue();
        then(jwtService).should().getEmail(validToken);
        then(commentService).should().deleteById(argumentCaptor.capture());
        assertEquals(commentId, argumentCaptor.getValue().getCommentId());
        assertEquals(userEmail, argumentCaptor.getValue().getUserEmail());
    }

    @Test
    void deleteComment_호출_실패_댓글_조회_실패_에러처리() throws Exception {
        //given
        String url = "/api/v1/comment/" + commentId;
        String validToken = "valid";

        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .success(false)
                .message("해당 댓글이 없습니다.")
                .build();

        ArgumentCaptor<CommentDeleteRequestDto> argumentCaptor = ArgumentCaptor.forClass(CommentDeleteRequestDto.class);
        given(authService.isLoggedIn()).willReturn(true);
        given(mockCookie.getName()).willReturn("token");
        given(mockCookie.getValue()).willReturn(validToken);
        given(jwtService.getEmail(validToken)).willReturn(userEmail);
        given(commentService.deleteById(any())).willThrow(new IllegalStateException("해당 댓글이 없습니다."));

        //when
        ResultActions resultActions = mockMvc.perform(delete(url)
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(mockCookie));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(content().bytes(objectMapper.writeValueAsBytes(exceptionResponse)));
        then(authService).should().isLoggedIn();
        then(mockCookie).should(times(2)).getName();
        then(mockCookie).should(times(3)).getValue();
        then(jwtService).should().getEmail(validToken);
        then(commentService).should().deleteById(argumentCaptor.capture());
        assertEquals(commentId, argumentCaptor.getValue().getCommentId());
        assertEquals(userEmail, argumentCaptor.getValue().getUserEmail());
    }

    @Test
    void deleteComment_호출_실패_타_작성자_에러처리() throws Exception {
        //given
        String url = "/api/v1/comment/" + commentId;
        String validToken = "valid";

        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .success(false)
                .message("해당 댓글의 작성자가 아닙니다.")
                .build();

        ArgumentCaptor<CommentDeleteRequestDto> argumentCaptor = ArgumentCaptor.forClass(CommentDeleteRequestDto.class);
        given(authService.isLoggedIn()).willReturn(true);
        given(mockCookie.getName()).willReturn("token");
        given(mockCookie.getValue()).willReturn(validToken);
        given(jwtService.getEmail(validToken)).willReturn(userEmail);
        given(commentService.deleteById(any())).willThrow(new IllegalStateException("해당 댓글의 작성자가 아닙니다."));

        //when
        ResultActions resultActions = mockMvc.perform(delete(url)
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(mockCookie));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(content().bytes(objectMapper.writeValueAsBytes(exceptionResponse)));
        then(authService).should().isLoggedIn();
        then(mockCookie).should(times(2)).getName();
        then(mockCookie).should(times(3)).getValue();
        then(jwtService).should().getEmail(validToken);
        then(commentService).should().deleteById(argumentCaptor.capture());
        assertEquals(userEmail, argumentCaptor.getValue().getUserEmail());
        assertEquals(commentId, argumentCaptor.getValue().getCommentId());
    }

    @Test
    void deleteComment_호출_로그인_상태_에러처리() throws Exception {
        //given
        String url = "/api/v1/comment/" + commentId;

        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .success(false)
                .message("로그인 상태가 아닙니다.")
                .build();

        given(authService.isLoggedIn()).willReturn(false);

        //when
        ResultActions resultActions = mockMvc.perform(delete(url)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(content().bytes(objectMapper.writeValueAsBytes(exceptionResponse)));
        then(authService).should().isLoggedIn();
    }
}
