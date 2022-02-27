package org.board.springboot.auth.controller;

import lombok.RequiredArgsConstructor;
import org.board.springboot.auth.dto.*;
import org.board.springboot.auth.service.AuthService;
import org.board.springboot.user.dto.UserSaveRequestDto;
import org.board.springboot.user.service.UserService;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AuthApiController {

    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/api/v1/auth/register")
    public RegisterResponseDto register(@RequestBody RegisterRequestDto requestDto) {
        UserSaveRequestDto userSaveRequestDto = UserSaveRequestDto.builder()
                .name(requestDto.getName())
                .email(requestDto.getEmail())
                .password(requestDto.getPassword())
                .build();

        Long id = userService.save(userSaveRequestDto);
        return RegisterResponseDto.builder()
                .success(true)
                .id(id)
                .build();
    }

    @PostMapping("/api/v1/auth/login")
    public LoginResponseDto login(@RequestBody LoginRequestDto requestDto) {
        LoginUserResponseDto loginUserResponseDto = authService.login(requestDto);
        return LoginResponseDto.builder()
                .success(true)
                .user(loginUserResponseDto)
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
