package org.board.springboot.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.board.springboot.auth.controller.AuthApiController;
import org.board.springboot.auth.dto.LoginRequestDto;
import org.board.springboot.auth.dto.LoginUserResponseDto;
import org.board.springboot.auth.dto.RegisterRequestDto;
import org.board.springboot.auth.service.AuthService;
import org.board.springboot.common.dto.ApiResponse;
import org.board.springboot.common.dto.ExceptionResponse;
import org.board.springboot.user.service.UserService;
import org.hibernate.tool.schema.spi.ExceptionHandler;
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
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(AuthApiController.class)
public class AuthApiControllerTest {

    @MockBean
    UserService userService;
    @MockBean
    AuthService authService;
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    private final String name = "jk";
    private final String email = "jk@jk.com";
    private final String password = "jkjk";

    @Test
    public void 유저등록_호출_성공() throws Exception {
        //given
        Long id = 1l;
        RegisterRequestDto registerRequestDto = RegisterRequestDto.builder()
                .name(name)
                .email(email)
                .password(password)
                .build();
        ApiResponse<Long> apiResponse = ApiResponse.<Long>builder()
                .success(true)
                .response(id)
                .build();
        String url = "http://localhost:8080/api/v1/auth/register";
        given(userService.save(any())).willReturn(id);

        //when
        ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(registerRequestDto)));

        //then
        result.andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(apiResponse)));
        then(userService).should().save(any());
    }

    @Test
    public void 유저등록_실패_이름_공백_예외처리() throws Exception {
        //given
        RegisterRequestDto registerRequestDto = RegisterRequestDto.builder()
                .name(null)
                .email(email)
                .password(password)
                .build();
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .success(false)
                .message("이름이 비어있습니다.")
                .build();

        String url = "http://localhost:8080/api/v1/auth/register";

        //when
        ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(registerRequestDto)));

        //then
        result.andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(exceptionResponse)));
    }

    @Test
    public void 유저등록_실패_이메일_공백_예외처리() throws Exception {
        //given
        RegisterRequestDto registerRequestDto = RegisterRequestDto.builder()
                .name(name)
                .email(null)
                .password(password)
                .build();
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .success(false)
                .message("이메일이 비어있습니다.")
                .build();

        String url = "http://localhost:8080/api/v1/auth/register";

        //when
        ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(registerRequestDto)));

        //then
        result.andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(exceptionResponse)));
    }

    @Test
    public void 유저등록_실패_비밀번호_공백_예외처리() throws Exception {
        //given
        RegisterRequestDto registerRequestDto = RegisterRequestDto.builder()
                .name(name)
                .email(email)
                .password(null)
                .build();
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .success(false)
                .message("비밀번호가 비어있습니다.")
                .build();

        String url = "http://localhost:8080/api/v1/auth/register";

        //when
        ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(registerRequestDto)));

        //then
        result.andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(exceptionResponse)));
    }

    @Test
    public void 로그인_호출_성공() throws Exception {
        //given
        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                .email(email)
                .password(password)
                .build();
        LoginUserResponseDto loginUserResponseDto = LoginUserResponseDto.builder()
                .name(name)
                .email(email)
                .build();
        ApiResponse<LoginUserResponseDto> apiResponse = ApiResponse.<LoginUserResponseDto>builder()
                .success(true)
                .response(loginUserResponseDto)
                .build();
        String url = "http://localhost:8080/api/v1/auth/login";
        given(authService.login(any())).willReturn(loginUserResponseDto);

        //when
        ResultActions resultActions = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(loginRequestDto)));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(apiResponse)));
        then(authService).should().login(any());
    }

    @Test
    public void 로그인_호출_실패_에러처리() throws Exception {
        //given
        String message = "해당 유저가 없습니다.";
        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                .email(email)
                .password(password)
                .build();
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .success(false)
                .message(message)
                .build();
        String url = "http://localhost:8080/api/v1/auth/login";
        given(authService.login(any())).willThrow(new IllegalArgumentException(message));

        //when
        ResultActions resultActions = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(loginRequestDto)));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(exceptionResponse)));
        then(authService).should().login(any());
    }

    @Test
    public void 로그인_호출_실패_이메일_공백_값_에러처리() throws Exception {
        //given
        String message = "아이디를 입력해주세요.";
        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                .email("")
                .password(password)
                .build();
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .success(false)
                .message(message)
                .build();
        String url = "http://localhost:8080/api/v1/auth/login";
        given(authService.login(any())).willThrow(new IllegalArgumentException(message));

        //when
        ResultActions resultActions = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(loginRequestDto)));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(exceptionResponse)));
        then(authService).should().login(any());
    }

    @Test
    public void 로그인_호출_실패_이메일_null_값_에러처리() throws Exception {
        //given
        String message = "아이디를 입력해주세요.";
        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                .email(null)
                .password(password)
                .build();
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .success(false)
                .message(message)
                .build();
        String url = "http://localhost:8080/api/v1/auth/login";
        given(authService.login(any())).willThrow(new IllegalArgumentException(message));

        //when
        ResultActions resultActions = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(loginRequestDto)));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(exceptionResponse)));
        then(authService).should().login(any());
    }

    @Test
    public void 로그아웃_호출_성공() throws Exception {
        //given
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .success(true)
                .response(null)
                .build();
        String url = "http://localhost:8080/api/v1/auth/logout";
        given(authService.logout()).willReturn(true);

        //when
        ResultActions resultActions = mockMvc.perform(post(url));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(apiResponse)));
        then(authService).should().logout();
    }

    @Test
    public void 로그아웃_호출_실패_에러처리() throws Exception {
        //given
        boolean success = false;
        String message = "로그인 상태가 아닙니다.";
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .success(success)
                .message(message)
                .build();
        String url = "http://localhost:8080/api/v1/auth/logout";
        given(authService.logout()).willThrow(new IllegalArgumentException(message));

        //when
        ResultActions resultActions = mockMvc.perform(post(url));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(exceptionResponse)));
        then(authService).should().logout();
    }

    @Test
    public void 로그인_상태_확인_호출_성공() throws Exception {
        //given
        ApiResponse<Boolean> apiResponse = ApiResponse.<Boolean>builder()
                .success(true)
                .response(true)
                .build();
        String url = "http://localhost:8080/api/v1/auth/logged-in";
        given(authService.isLoggedIn()).willReturn(true);

        //when
        ResultActions resultActions = mockMvc.perform(get(url));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(apiResponse)));
        then(authService).should().isLoggedIn();
    }

    @Test
    public void 로그인_상태_확인_호출_false_반환() throws Exception {
        //given
        ApiResponse<Boolean> apiResponse = ApiResponse.<Boolean>builder()
                .success(true)
                .response(false)
                .build();
        String url = "http://localhost:8080/api/v1/auth/logged-in";
        given(authService.isLoggedIn()).willReturn(false);

        //when
        ResultActions resultActions = mockMvc.perform(get(url));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(apiResponse)));
        then(authService).should().isLoggedIn();
    }
}
