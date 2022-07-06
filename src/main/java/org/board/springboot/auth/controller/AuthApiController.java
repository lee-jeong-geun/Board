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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class AuthApiController {

    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/api/v1/auth/register")
    public ApiResponse<Long> register(@RequestBody RegisterRequestDto requestDto) {
        if (!StringUtils.hasText(requestDto.getName())) {
            throw new IllegalArgumentException("이름이 비어있습니다.");
        }
        if (!StringUtils.hasText(requestDto.getEmail())) {
            throw new IllegalArgumentException("이메일이 비어있습니다.");
        }

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
        LoginUserResponseDto loginUserResponseDto = authService.login(requestDto);
        return ApiResponse.<LoginUserResponseDto>builder()
                .success(true)
                .response(loginUserResponseDto)
                .build();
    }

    @PostMapping("/api/v1/auth/logout")
    public ApiResponse<Void> logout() {
        authService.logout();
        return ApiResponse.<Void>builder()
                .success(true)
                .response(null)
                .build();
    }

    @GetMapping("/api/v1/auth/logged-in")
    public ApiResponse<Boolean> isLoggedIn() {
        return ApiResponse.<Boolean>builder()
                .success(true)
                .response(authService.isLoggedIn())
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
