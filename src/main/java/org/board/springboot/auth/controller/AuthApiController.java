package org.board.springboot.auth.controller;

import lombok.RequiredArgsConstructor;
import org.board.springboot.auth.dto.LoginRequestDto;
import org.board.springboot.auth.dto.LoginUserResponseDto;
import org.board.springboot.auth.dto.RegisterRequestDto;
import org.board.springboot.auth.service.AuthService;
import org.board.springboot.common.dto.ApiResponse;
import org.board.springboot.common.dto.ExceptionResponse;
import org.board.springboot.user.dto.UserSaveRequestDto;
import org.board.springboot.user.service.UserService;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RestController
public class AuthApiController {

    private final UserService userService;
    private final AuthService authService;
    private final HttpServletRequest httpServletRequest;

    @PostMapping("/api/v1/auth/register")
    public ApiResponse<Long> register(@RequestBody RegisterRequestDto requestDto) {
        UserSaveRequestDto userSaveRequestDto = UserSaveRequestDto.builder()
                .name(requestDto.getName())
                .email(requestDto.getEmail())
                .password(requestDto.getPassword())
                .build();

        return ApiResponse.<Long>builder()
                .success(true)
                .response(userService.save(userSaveRequestDto))
                .build();
    }

    @PostMapping("/api/v1/auth/login")
    public ApiResponse<LoginUserResponseDto> login(@RequestBody LoginRequestDto requestDto) {
        LoginUserResponseDto loginUserResponseDto = authService.login(requestDto, httpServletRequest.getSession());
        return ApiResponse.<LoginUserResponseDto>builder()
                .success(true)
                .response(loginUserResponseDto)
                .build();
    }

    @PostMapping("/api/v1/auth/logout")
    public ApiResponse<Void> logout() {
        authService.logout(httpServletRequest.getSession());
        return ApiResponse.<Void>builder()
                .success(true)
                .response(null)
                .build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ExceptionResponse IllegalArgumentExceptionHandler(Exception exception) {
        return ExceptionResponse.builder()
                .success(false)
                .message(exception.getMessage())
                .build();
    }
}
