package org.board.springboot.posts;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.BDDAssertions;
import org.board.springboot.auth.service.AuthService;
import org.board.springboot.auth.service.JWTService;
import org.board.springboot.common.dto.ApiResponse;
import org.board.springboot.common.dto.ExceptionResponse;
import org.board.springboot.posts.controller.PostsApiController;
import org.board.springboot.posts.dto.PostsFindResponseDto;
import org.board.springboot.posts.dto.PostsSaveRequestBody;
import org.board.springboot.posts.dto.PostsSaveRequestDto;
import org.board.springboot.posts.service.PostsService;
import org.board.springboot.redis.user.UserSessionService;
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
import static org.mockito.BDDMockito.*;
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
    @MockBean
    private UserSessionService userSessionService;
    @MockBean
    private AuthService authService;
    @MockBean
    private JWTService jwtService;
    @Mock
    private MockCookie mockCookie;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    private final String title = "title";
    private final String content = "content";
    private final String email = "jk@jk.com";

    @Test
    public void 게시글_리스트_조회_성공() throws Exception {
        //given
        int viewCount = 0;
        String url = "/api/v1/posts";
        PostsFindResponseDto postsFindResponseDto = PostsFindResponseDto.builder()
                .postsId(1l)
                .title(title)
                .content(content)
                .viewCount(viewCount)
                .userEmail(email)
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
        given(authService.isLoggedIn()).willReturn(true);
        given(mockCookie.getName()).willReturn("token");
        given(mockCookie.getValue()).willReturn("valid");
        given(jwtService.getEmail("valid")).willReturn(email);
        given(postsService.save(any())).willReturn(id);

        //when
        ResultActions resultActions = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(postsSaveRequestBody))
                .cookie(mockCookie));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(apiResponse)));
        then(authService).should().isLoggedIn();
        then(mockCookie).should(times(2)).getName();
        then(mockCookie).should(times(3)).getValue();
        then(jwtService).should().getEmail("valid");
        then(postsService).should().save(argumentCaptor.capture());
        then(userSessionService).should().checkTodayRemainPostsCount(email);
        then(userSessionService).should().checkLastPostsSaveTime(email);
        then(userSessionService).should().updateTodayRemainPostsCount(email);
        then(userSessionService).should().updateLastPostsSaveTime(email);
        BDDAssertions.then(argumentCaptor.getValue().getTitle()).isEqualTo(title);
        BDDAssertions.then(argumentCaptor.getValue().getContent()).isEqualTo(content);
        BDDAssertions.then(argumentCaptor.getValue().getEmail()).isEqualTo(email);
    }

    @Test
    public void 게시글_등록_실패_일일_게시글_최대상태_에러처리() throws Exception {
        //given
        String url = "/api/v1/posts";
        boolean success = false;
        long id = 1;
        PostsSaveRequestBody postsSaveRequestBody = PostsSaveRequestBody.builder()
                .title(title)
                .content(content)
                .build();
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .success(success)
                .message("오늘은 더이상 게시글을 올릴 수 없습니다.")
                .build();
        given(authService.isLoggedIn()).willReturn(true);
        given(mockCookie.getName()).willReturn("token");
        given(mockCookie.getValue()).willReturn("valid");
        given(jwtService.getEmail("valid")).willReturn(email);
        given(postsService.save(any())).willReturn(id);
        willThrow(new IllegalStateException("오늘은 더이상 게시글을 올릴 수 없습니다.")).given(userSessionService).checkTodayRemainPostsCount(email);

        //when
        ResultActions resultActions = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(postsSaveRequestBody))
                .cookie(mockCookie));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(exceptionResponse)));
        then(authService).should().isLoggedIn();
        then(mockCookie).should(times(2)).getName();
        then(mockCookie).should(times(3)).getValue();
        then(jwtService).should().getEmail("valid");
        then(userSessionService).should().checkTodayRemainPostsCount(email);
    }

    @Test
    public void 게시글_등록_실패_게시글_간격_5초_미만_에러처리() throws Exception {
        //given
        String url = "/api/v1/posts";
        boolean success = false;
        long id = 1;
        PostsSaveRequestBody postsSaveRequestBody = PostsSaveRequestBody.builder()
                .title(title)
                .content(content)
                .build();
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .success(success)
                .message("게시글은 4초 뒤에 작성 가능합니다.")
                .build();
        given(authService.isLoggedIn()).willReturn(true);
        given(mockCookie.getName()).willReturn("token");
        given(mockCookie.getValue()).willReturn("valid");
        given(jwtService.getEmail("valid")).willReturn(email);
        given(postsService.save(any())).willReturn(id);
        willThrow(new IllegalStateException("게시글은 4초 뒤에 작성 가능합니다.")).given(userSessionService).checkLastPostsSaveTime(email);

        //when
        ResultActions resultActions = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(postsSaveRequestBody))
                .cookie(mockCookie));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(exceptionResponse)));
        then(authService).should().isLoggedIn();
        then(mockCookie).should(times(2)).getName();
        then(mockCookie).should(times(3)).getValue();
        then(jwtService).should().getEmail("valid");
        then(userSessionService).should().checkTodayRemainPostsCount(email);
        then(userSessionService).should().checkLastPostsSaveTime(email);
    }

    @Test
    public void 게시글_등록_실패_로그인상태_에러처리() throws Exception {
        //given
        String url = "/api/v1/posts";
        PostsSaveRequestBody postsSaveRequestBody = PostsSaveRequestBody.builder()
                .title(title)
                .content(content)
                .build();
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .success(false)
                .message("로그인 상태가 아닙니다.")
                .build();
        given(authService.isLoggedIn()).willReturn(false);

        //when
        ResultActions resultActions = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(postsSaveRequestBody))
                .cookie(mockCookie));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(exceptionResponse)));
        then(authService).should().isLoggedIn();
    }

    @Test
    public void 게시글_조회_아이디_성공() throws Exception {
        //given
        String url = "/api/v1/posts/1";
        Long id = 1l;
        int viewCount = 0;
        int updateCount = 1;
        PostsFindResponseDto postsFindResponseDto = PostsFindResponseDto.builder()
                .postsId(id)
                .title(title)
                .content(content)
                .viewCount(viewCount + updateCount)
                .userEmail(email)
                .build();
        ApiResponse<PostsFindResponseDto> apiResponse = ApiResponse.<PostsFindResponseDto>builder()
                .success(true)
                .response(postsFindResponseDto)
                .build();
        given(postsService.findById(id)).willReturn(postsFindResponseDto);

        //when
        ResultActions resultActions = mockMvc.perform(get(url));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(apiResponse)));
        then(postsService).should().viewCountUpdateById(id, updateCount);
        then(postsService).should().findById(id);
    }

    @Test
    public void 게시글_조회_아이디_findById_실패_에러처리() throws Exception {
        //given
        String url = "/api/v1/posts/1";
        Long id = 1l;
        int updateCount = 1;
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .success(false)
                .message("해당 게시글이 없습니다.")
                .build();
        given(postsService.findById(id)).willThrow(new IllegalStateException("해당 게시글이 없습니다."));

        //when
        ResultActions resultActions = mockMvc.perform(get(url));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(exceptionResponse)));
        then(postsService).should().viewCountUpdateById(id, updateCount);
        then(postsService).should().findById(id);
    }

    @Test
    public void 게시글_조회_아이디_viewCountUpdateById_실패_에러처리() throws Exception {
        //given
        String url = "/api/v1/posts/1";
        Long id = 1l;
        int updateCount = 1;
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .success(false)
                .message("해당 게시글이 없습니다.")
                .build();
        given(postsService.viewCountUpdateById(id, updateCount)).willThrow(new IllegalStateException("해당 게시글이 없습니다."));

        //when
        ResultActions resultActions = mockMvc.perform(get(url));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(exceptionResponse)));
        then(postsService).should().viewCountUpdateById(id, updateCount);
    }
}
