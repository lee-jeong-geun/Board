package org.board.springboot.comment;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.BDDAssertions;
import org.board.springboot.comment.controller.CommentApiController;
import org.board.springboot.comment.dto.CommentSaveRequestDto;
import org.board.springboot.comment.service.CommentService;
import org.board.springboot.common.dto.ApiResponse;
import org.board.springboot.common.dto.ExceptionResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(CommentApiController.class)
public class CommentApiControllerTest {

    @MockBean
    private CommentService commentService;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private final String content = "content";
    private final String userEmail = "jk@jk.com";

    @Test
    public void Comment_save_호출_성공() throws Exception {
        //given
        String url = "/api/v1/comment";
        Long postsId = 1l;
        Long commentId = 1l;

        CommentSaveRequestDto commentSaveRequestDto = CommentSaveRequestDto.builder()
                .content(content)
                .userEmail(userEmail)
                .postsId(postsId)
                .build();

        ApiResponse<Long> apiResponse = ApiResponse.<Long>builder()
                .success(true)
                .response(commentId)
                .build();
        ArgumentCaptor<CommentSaveRequestDto> argumentCaptor = ArgumentCaptor.forClass(CommentSaveRequestDto.class);
        given(commentService.save(any())).willReturn(commentId);

        //when
        ResultActions resultActions = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(commentSaveRequestDto)));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(apiResponse)));
        then(commentService).should().save(argumentCaptor.capture());
        BDDAssertions.then(argumentCaptor.getValue().getContent()).isEqualTo(content);
        BDDAssertions.then(argumentCaptor.getValue().getUserEmail()).isEqualTo(userEmail);
        BDDAssertions.then(argumentCaptor.getValue().getPostsId()).isEqualTo(postsId);
    }

    @Test
    public void Comment_save_호출_실패_잘못된_userEmail_에러처리() throws Exception {
        //given
        String url = "/api/v1/comment";
        Long postsId = 1l;

        CommentSaveRequestDto commentSaveRequestDto = CommentSaveRequestDto.builder()
                .content(content)
                .userEmail(userEmail)
                .postsId(postsId)
                .build();
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .success(false)
                .message("해당 유저가 없습니다.")
                .build();

        given(commentService.save(any())).willThrow(new IllegalArgumentException("해당 유저가 없습니다."));

        //when
        ResultActions resultActions = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(commentSaveRequestDto)));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(exceptionResponse)));
        then(commentService).should().save(any());
    }

    @Test
    public void Comment_save_호출_실패_잘못된_postsId_에러처리() throws Exception {
        //given
        String url = "/api/v1/comment";
        Long postsId = 1l;

        CommentSaveRequestDto commentSaveRequestDto = CommentSaveRequestDto.builder()
                .content(content)
                .userEmail(userEmail)
                .postsId(postsId)
                .build();
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .success(false)
                .message("해당 게시글이 없습니다.")
                .build();

        given(commentService.save(any())).willThrow(new IllegalStateException("해당 게시글이 없습니다."));

        //when
        ResultActions resultActions = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(commentSaveRequestDto)));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(exceptionResponse)));
        then(commentService).should().save(any());
    }
}
