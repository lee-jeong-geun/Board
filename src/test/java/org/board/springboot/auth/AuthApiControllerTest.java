package org.board.springboot.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.board.springboot.auth.controller.AuthApiController;
import org.board.springboot.auth.dto.*;
import org.board.springboot.auth.service.AuthService;
import org.board.springboot.user.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.servlet.http.HttpServletRequest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RunWith(SpringRunner.class)
@WebMvcTest(AuthApiController.class)
public class AuthApiControllerTest {

    @MockBean
    UserService userService;

    @MockBean
    AuthService authService;

    @Mock
    HttpServletRequest httpServletRequest;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void 유저등록_호출_성공() throws Exception {
        //given
        String name = "jk";
        String email = "jk@jk.com";
        String password = "jkjk";
        boolean success = true;
        Long id = 1l;
        RegisterRequestDto registerRequestDto = RegisterRequestDto.builder()
                .name(name)
                .email(email)
                .password(password)
                .build();
        RegisterResponseDto registerResponseDto = RegisterResponseDto.builder()
                .success(success)
                .id(id)
                .build();
        String url = "http://localhost:8080/api/v1/auth/register";
        given(userService.save(any())).willReturn(id);

        //when
        ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(new ObjectMapper().writeValueAsString(registerRequestDto)));

        //then
        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(new ObjectMapper().writeValueAsString(registerResponseDto)));
    }

    @Test
    public void 로그인_호출_성공() throws Exception {
        //given
        String name = "jk";
        String email = "jk@jk.com";
        String password = "jkjk";

        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                .email(email)
                .password(password)
                .build();
        LoginUserResponseDto loginUserResponseDto = LoginUserResponseDto.builder()
                .name(name)
                .email(email)
                .build();
        LoginResponseDto loginResponseDto = LoginResponseDto.builder()
                .success(true)
                .user(loginUserResponseDto)
                .build();
        String url = "http://localhost:8080/api/v1/auth/login";
        given(authService.login(any())).willReturn(loginUserResponseDto);

        //when
        ResultActions resultActions = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(new ObjectMapper().writeValueAsString(loginRequestDto)));

        //then
        resultActions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(new ObjectMapper().writeValueAsString(loginResponseDto)));
    }

    @Test
    public void 로그인_호출_실패_에러처리() throws Exception {
        //given
        String email = "jk@jk.com";
        String password = "jkjk";
        String message = "해당 유저가 없습니다.";
        boolean success = false;
        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                .email(email)
                .password(password)
                .build();
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .success(success)
                .message(message)
                .build();
        String url = "http://localhost:8080/api/v1/auth/login";
        given(authService.login(any())).willThrow(new IllegalArgumentException(message));

        //when
        ResultActions resultActions = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(new ObjectMapper().writeValueAsString(loginRequestDto)));

        //then
        resultActions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(new ObjectMapper().writeValueAsString(exceptionResponse)));
    }

    @Test
    public void logout_호출_성공() throws Exception {
        //given
        LogoutResponseDto logoutResponseDto = LogoutResponseDto.builder()
                .success(true)
                .build();
        String url = "http://localhost:8080/api/v1/auth/logout";
        given(authService.logout(httpServletRequest.getSession())).willReturn(true);

        //when
        ResultActions resultActions = mockMvc.perform(post(url));

        //then
        resultActions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(new ObjectMapper().writeValueAsString(logoutResponseDto)));
    }
}
