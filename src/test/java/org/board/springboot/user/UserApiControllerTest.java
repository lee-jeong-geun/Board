package org.board.springboot.user;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.board.springboot.common.dto.ApiResponse;
import org.board.springboot.common.dto.ExceptionResponse;
import org.board.springboot.posts.domain.Posts;
import org.board.springboot.user.controller.UserApiController;
import org.board.springboot.user.domain.User;
import org.board.springboot.user.dto.UserAndPostsFindResponseDto;
import org.board.springboot.user.dto.UserSaveRequestDto;
import org.board.springboot.user.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(UserApiController.class)
public class UserApiControllerTest {

    @MockBean
    private UserService userService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    private final String email = "jk@jk.com";

    @Test
    public void 유저저장_호출_성공() throws Exception {
        //given
        String name = "jk";
        String password = "jkjk";
        UserSaveRequestDto userSaveRequestDto = UserSaveRequestDto.builder()
                .name(name)
                .email(email)
                .password(password)
                .build();

        String url = "http://localhost:8080/api/v1/users";
        given(userService.save(any())).willReturn(1l);

        //when
        ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(userSaveRequestDto)));

        //then
        result.andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    @Test
    public void 유저_게시글_호출_성공() throws Exception {
        //given
        String url = "http://localhost:8080/api/v1/users/" + email;
        User user = User.builder()
                .email(email)
                .build();
        Posts posts1 = Posts.builder()
                .title("title1")
                .content("content1")
                .user(user)
                .build();
        Posts posts2 = Posts.builder()
                .title("title2")
                .content("content2")
                .user(user)
                .build();
        user.getPostsList().add(posts1);
        user.getPostsList().add(posts2);
        UserAndPostsFindResponseDto userAndPostsFindResponseDto = UserAndPostsFindResponseDto.builder()
                .user(user)
                .build();
        given(userService.findByEmail(email)).willReturn(user);

        //when
        ResultActions resultActions = mockMvc.perform(get(url)
                .contentType(MediaType.APPLICATION_JSON_UTF8));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(ApiResponse.builder()
                        .success(true)
                        .response(userAndPostsFindResponseDto)
                        .build())));
    }

    @Test
    public void 유저_게시글_호출_실패_에러처리() throws Exception {
        //given
        String url = "http://localhost:8080/api/v1/users/" + email;
        String message = "해당 유저가 없습니다.";
        given(userService.findByEmail(email)).willThrow(new IllegalArgumentException(message));

        //when
        ResultActions resultActions = mockMvc.perform(get(url)
                .contentType(MediaType.APPLICATION_JSON_UTF8));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(ExceptionResponse.builder()
                        .success(false)
                        .message(message)
                        .build())));
    }
}
