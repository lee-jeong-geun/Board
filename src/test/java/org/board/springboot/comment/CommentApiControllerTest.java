package org.board.springboot.comment;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.BDDAssertions;
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
    private final Long commentId = 1L;

    @Test
    public void Comment_save_??????_??????() throws Exception {
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
    public void Comment_save_??????_??????_?????????_postsId_????????????() throws Exception {
        //given
        String url = "/api/v1/comment";
        String validToken = "valid";

        CommentSaveRequestBody commentSaveRequestBody = CommentSaveRequestBody.builder()
                .content(content)
                .postsId(postsId)
                .build();
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .success(false)
                .message("?????? ???????????? ????????????.")
                .build();

        ArgumentCaptor<CommentSaveRequestDto> argumentCaptor = ArgumentCaptor.forClass(CommentSaveRequestDto.class);
        given(commentService.save(any())).willThrow(new IllegalStateException("?????? ???????????? ????????????."));
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
    public void Comment_save_??????_?????????_??????_????????????() throws Exception {
        //given
        String url = "/api/v1/comment";

        CommentSaveRequestBody commentSaveRequestBody = CommentSaveRequestBody.builder()
                .content(content)
                .postsId(postsId)
                .build();
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .success(false)
                .message("????????? ????????? ????????????.")
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
    public void getComments_??????_??????() throws Exception {
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
                .contentType(MediaType.APPLICATION_JSON_UTF8));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(apiResponse)));
        then(commentService).should().findByPostsId(postsId);
    }

    @Test
    public void getComments_??????_??????_?????????_??????_??????_????????????() throws Exception {
        //given
        String url = "/api/v1/comment/" + postsId;

        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .success(false)
                .message("?????? ???????????? ????????????.")
                .build();

        given(commentService.findByPostsId(postsId)).willThrow(new IllegalStateException("?????? ???????????? ????????????."));

        //when
        ResultActions resultActions = mockMvc.perform(get(url)
                .contentType(MediaType.APPLICATION_JSON_UTF8));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(exceptionResponse)));
    }

    @Test
    public void deleteComment_??????_??????() throws Exception {
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
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .cookie(mockCookie));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(apiResponse)));
        then(authService).should().isLoggedIn();
        then(mockCookie).should(times(2)).getName();
        then(mockCookie).should(times(3)).getValue();
        then(jwtService).should().getEmail(validToken);
        then(commentService).should().deleteById(argumentCaptor.capture());
        BDDAssertions.then(argumentCaptor.getValue().getCommentId()).isEqualTo(commentId);
        BDDAssertions.then(argumentCaptor.getValue().getUserEmail()).isEqualTo(userEmail);
    }

    @Test
    public void deleteComment_??????_??????_??????_??????_??????_????????????() throws Exception {
        //given
        String url = "/api/v1/comment/" + commentId;
        String validToken = "valid";

        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .success(false)
                .message("?????? ????????? ????????????.")
                .build();

        ArgumentCaptor<CommentDeleteRequestDto> argumentCaptor = ArgumentCaptor.forClass(CommentDeleteRequestDto.class);
        given(authService.isLoggedIn()).willReturn(true);
        given(mockCookie.getName()).willReturn("token");
        given(mockCookie.getValue()).willReturn(validToken);
        given(jwtService.getEmail(validToken)).willReturn(userEmail);
        given(commentService.deleteById(any())).willThrow(new IllegalStateException("?????? ????????? ????????????."));

        //when
        ResultActions resultActions = mockMvc.perform(delete(url)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .cookie(mockCookie));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(exceptionResponse)));
        then(authService).should().isLoggedIn();
        then(mockCookie).should(times(2)).getName();
        then(mockCookie).should(times(3)).getValue();
        then(jwtService).should().getEmail(validToken);
        then(commentService).should().deleteById(argumentCaptor.capture());
        BDDAssertions.then(argumentCaptor.getValue().getCommentId()).isEqualTo(commentId);
        BDDAssertions.then(argumentCaptor.getValue().getUserEmail()).isEqualTo(userEmail);
    }

    @Test
    public void deleteComment_??????_??????_???_?????????_????????????() throws Exception {
        //given
        String url = "/api/v1/comment/" + commentId;
        String validToken = "valid";

        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .success(false)
                .message("?????? ????????? ???????????? ????????????.")
                .build();

        ArgumentCaptor<CommentDeleteRequestDto> argumentCaptor = ArgumentCaptor.forClass(CommentDeleteRequestDto.class);
        given(authService.isLoggedIn()).willReturn(true);
        given(mockCookie.getName()).willReturn("token");
        given(mockCookie.getValue()).willReturn(validToken);
        given(jwtService.getEmail(validToken)).willReturn(userEmail);
        given(commentService.deleteById(any())).willThrow(new IllegalStateException("?????? ????????? ???????????? ????????????."));

        //when
        ResultActions resultActions = mockMvc.perform(delete(url)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .cookie(mockCookie));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(exceptionResponse)));
        then(authService).should().isLoggedIn();
        then(mockCookie).should(times(2)).getName();
        then(mockCookie).should(times(3)).getValue();
        then(jwtService).should().getEmail(validToken);
        then(commentService).should().deleteById(argumentCaptor.capture());
        BDDAssertions.then(argumentCaptor.getValue().getUserEmail()).isEqualTo(userEmail);
        BDDAssertions.then(argumentCaptor.getValue().getCommentId()).isEqualTo(commentId);
    }

    @Test
    public void deleteComment_??????_?????????_??????_????????????() throws Exception {
        //given
        String url = "/api/v1/comment/" + commentId;

        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .success(false)
                .message("????????? ????????? ????????????.")
                .build();

        given(authService.isLoggedIn()).willReturn(false);

        //when
        ResultActions resultActions = mockMvc.perform(delete(url)
                .contentType(MediaType.APPLICATION_JSON_UTF8));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(exceptionResponse)));
        then(authService).should().isLoggedIn();
    }
}
