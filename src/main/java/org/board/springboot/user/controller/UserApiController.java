package org.board.springboot.user.controller;

import lombok.RequiredArgsConstructor;
import org.board.springboot.user.dto.UserFindPostsListResponseDto;
import org.board.springboot.user.dto.UserSaveRequestDto;
import org.board.springboot.user.service.UserService;
import org.board.springboot.common.dto.ApiResponse;
import org.board.springboot.common.dto.ExceptionResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class UserApiController {

    private final UserService userService;

    @PostMapping("/api/v1/users")
    public Long save(@RequestBody UserSaveRequestDto requestDto) {
        return userService.save(requestDto);
    }

    @GetMapping("/api/v1/users/{id}")
    public ApiResponse<List<UserFindPostsListResponseDto>> findPostsById(@PathVariable Long id) {
        return ApiResponse.<List<UserFindPostsListResponseDto>>builder()
                .success(true)
                .response(userService.findPostsById(id))
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
