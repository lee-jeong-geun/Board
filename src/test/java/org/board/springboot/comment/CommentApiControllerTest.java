package org.board.springboot.comment;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.BDDAssertions;
import org.board.springboot.comment.controller.CommentApiController;
import org.board.springboot.comment.dto.CommentSaveRequestDto;
import org.board.springboot.comment.service.CommentService;
import org.board.springboot.common.dto.ApiResponse;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    @Test
    public void Comment_save_호출_성공() throws Exception {
        //given
        String url = "/api/v1/comment";
        String content = "content";
        String userEmail = "jk@jk.com";
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
                .andExpect(MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(apiResponse)));
        then(commentService).should().save(argumentCaptor.capture());
        BDDAssertions.then(argumentCaptor.getValue().getContent()).isEqualTo(content);
        BDDAssertions.then(argumentCaptor.getValue().getUserEmail()).isEqualTo(userEmail);
        BDDAssertions.then(argumentCaptor.getValue().getPostsId()).isEqualTo(postsId);
    }
}
