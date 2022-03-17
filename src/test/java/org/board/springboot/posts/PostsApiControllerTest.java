package org.board.springboot.posts;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.BDDAssertions;
import org.board.springboot.common.dto.ApiResponse;
import org.board.springboot.common.dto.ExceptionResponse;
import org.board.springboot.posts.controller.PostsApiController;
import org.board.springboot.posts.dto.PostsFindResponseDto;
import org.board.springboot.posts.dto.PostsSaveRequestBody;
import org.board.springboot.posts.dto.PostsSaveRequestDto;
import org.board.springboot.posts.service.PostsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(PostsApiController.class)
public class PostsApiControllerTest {

    @MockBean
    private PostsService postsService;

    @Mock
    private MockHttpSession mockHttpSession;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void 게시글_리스트_조회_성공() throws Exception {
        //given
        String url = "/api/v1/posts";
        PostsFindResponseDto postsFindResponseDto = PostsFindResponseDto.builder()
                .title("title")
                .content("content")
                .userName("jk")
                .build();
        List<PostsFindResponseDto> list = new ArrayList<>();
        list.add(postsFindResponseDto);
        ApiResponse<List<PostsFindResponseDto>> apiResponse = ApiResponse.<List<PostsFindResponseDto>>builder()
                .success(true)
                .response(list)
                .build();
        given(postsService.findAll()).willReturn(list);

        //when
        ResultActions resultActions = mockMvc.perform(get(url));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(apiResponse)));
    }

    @Test
    public void 게시글_등록_성공() throws Exception {
        //given
        String title = "title";
        String content = "content";
        String email = "jk@jk.com";
        String url = "/api/v1/posts";
        boolean success = true;
        long id = 1;
        PostsSaveRequestBody postsSaveRequestBody = PostsSaveRequestBody.builder()
                .title(title)
                .content(content)
                .build();
        ApiResponse<Long> apiResponse = ApiResponse.<Long>builder()
                .success(success)
                .response(id)
                .build();
        ArgumentCaptor<PostsSaveRequestDto> argumentCaptor = ArgumentCaptor.forClass(PostsSaveRequestDto.class);
        given(mockHttpSession.getAttribute("login")).willReturn(email);
        given(postsService.save(any())).willReturn(id);

        //when
        ResultActions resultActions = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(postsSaveRequestBody))
                .session(mockHttpSession));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(apiResponse)));
        then(mockHttpSession).should(times(2)).getAttribute("login");
        then(postsService).should().save(argumentCaptor.capture());
        BDDAssertions.then(argumentCaptor.getValue().getTitle()).isEqualTo(title);
        BDDAssertions.then(argumentCaptor.getValue().getContent()).isEqualTo(content);
        BDDAssertions.then(argumentCaptor.getValue().getEmail()).isEqualTo(email);
    }

    @Test
    public void 게시글_등록_실패_로그인상태_에러처리() throws Exception {
        //given
        String url = "/api/v1/posts";
        PostsSaveRequestBody postsSaveRequestBody = PostsSaveRequestBody.builder()
                .title("title")
                .content("content")
                .build();
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .success(false)
                .message("로그인 상태가 아닙니다.")
                .build();
        given(mockHttpSession.getAttribute("login")).willReturn(null);

        //when
        ResultActions resultActions = mockMvc.perform(post(url)
                .session(mockHttpSession)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(postsSaveRequestBody)));

        //then
        then(mockHttpSession).should().getAttribute("login");
        resultActions.andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(exceptionResponse)));
    }
}
