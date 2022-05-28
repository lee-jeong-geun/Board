package org.board.springboot.comment;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.BDDAssertions;
import org.board.springboot.auth.service.AuthService;
import org.board.springboot.auth.service.JWTService;
import org.board.springboot.comment.controller.CommentApiController;
import org.board.springboot.comment.dto.CommentFindResponseDto;
import org.board.springboot.comment.dto.CommentSaveRequestBody;
import org.board.springboot.comment.dto.CommentSaveRequestDto;
import org.board.springboot.comment.service.CommentService;
import org.board.springboot.common.dto.ApiResponse;
import org.board.springboot.common.dto.ExceptionResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockCookie;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(CommentApiController.class)
public class CommentApiControllerTest {

    @MockBean
    private CommentService commentService;
    @MockBean
    private AuthService authService;
    @MockBean
    private JWTService jwtService;
    @Mock
    private MockCookie mockCookie;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private final String content = "content";
    private final String userEmail = "jk@jk.com";
    private final Long postsId = 1l;

    @Test
    public void Comment_save_호출_성공() throws Exception {
        //given
        String url = "/api/v1/comment";
        String validToken = "valid";
        Long commentId = 1l;

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
                .contentType(MediaType.APPLICATION_JSON_UTF8)
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
        BDDAssertions.then(argumentCaptor.getValue().getContent()).isEqualTo(content);
        BDDAssertions.then(argumentCaptor.getValue().getUserEmail()).isEqualTo(userEmail);
        BDDAssertions.then(argumentCaptor.getValue().getPostsId()).isEqualTo(postsId);
    }

    @Test
    public void Comment_save_호출_실패_잘못된_postsId_에러처리() throws Exception {
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
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(commentSaveRequestBody))
                .cookie(mockCookie));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(exceptionResponse)));
        then(authService).should().isLoggedIn();
        then(mockCookie).should(times(2)).getName();
        then(mockCookie).should(times(3)).getValue();
        then(jwtService).should().getEmail(validToken);
        then(commentService).should().save(argumentCaptor.capture());
        BDDAssertions.then(argumentCaptor.getValue().getContent()).isEqualTo(content);
        BDDAssertions.then(argumentCaptor.getValue().getUserEmail()).isEqualTo(userEmail);
        BDDAssertions.then(argumentCaptor.getValue().getPostsId()).isEqualTo(postsId);
    }

    @Test
    public void Comment_save_호출_로그인_상태_에러처리() throws Exception {
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
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(commentSaveRequestBody)));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(exceptionResponse)));
        then(authService).should().isLoggedIn();
    }

    @Test
    public void getComments_호출_성공() throws Exception {
        //given
        String url = "/api/v1/comment/" + postsId;
        Long commentId = 1L;
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
                .contentType(MediaType.APPLICATION_JSON_UTF8));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(apiResponse)));
        then(commentService).should().findByPostsId(postsId);
    }

    @Test
    public void getComments_호출_실패_게시글_조회_실패_에러처리() throws Exception {
        //given
        String url = "/api/v1/comment/" + postsId;

        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .success(false)
                .message("해당 게시글이 없습니다.")
                .build();

        given(commentService.findByPostsId(postsId)).willThrow(new IllegalStateException("해당 게시글이 없습니다."));

        //when
        ResultActions resultActions = mockMvc.perform(get(url)
                .contentType(MediaType.APPLICATION_JSON_UTF8));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(exceptionResponse)));
    }

    @Test
    public void deleteComment_호출_성공() throws Exception {
        //given
        Long commentId = 1L;
        String url = "/api/v1/comment/" + commentId;

        ApiResponse<Long> apiResponse = ApiResponse.<Long>builder()
                .success(true)
                .response(commentId)
                .build();

        given(commentService.deleteById(commentId)).willReturn(commentId);

        //when
        ResultActions resultActions = mockMvc.perform(delete(url)
                .contentType(MediaType.APPLICATION_JSON_UTF8));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(apiResponse)));
        then(commentService).should().deleteById(commentId);
    }

    @Test
    public void deleteComment_호출_실패_댓글_조회_실패_에러처리() throws Exception {
        //given
        Long commentId = 1L;
        String url = "/api/v1/comment/" + commentId;

        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .success(false)
                .message("해당 댓글이 없습니다.")
                .build();

        given(commentService.deleteById(commentId)).willThrow(new IllegalStateException("해당 댓글이 없습니다."));

        //when
        ResultActions resultActions = mockMvc.perform(delete(url)
                .contentType(MediaType.APPLICATION_JSON_UTF8));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(exceptionResponse)));
        then(commentService).should().deleteById(commentId);
    }
}
