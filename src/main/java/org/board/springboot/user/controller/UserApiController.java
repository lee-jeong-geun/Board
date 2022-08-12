package org.board.springboot.user.controller;

import lombok.RequiredArgsConstructor;
import org.board.springboot.common.dto.ApiResponse;
import org.board.springboot.common.dto.ExceptionResponse;
import org.board.springboot.user.dto.UserAndPostsFindResponseDto;
import org.board.springboot.user.dto.UserSaveRequestDto;
import org.board.springboot.user.dto.UserUpdateModel;
import org.board.springboot.user.dto.UserUpdateRequestDto;
import org.board.springboot.user.service.UserService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class UserApiController {

    private final UserService userService;

    @PostMapping("/api/v1/users")
    public Long save(@RequestBody UserSaveRequestDto requestDto) {
        if (requestDto.getEmail() == null || requestDto.getEmail().length() == 0) {
            throw new IllegalArgumentException("이메일이 비어있습니다.");
        }

        return userService.save(requestDto);
    }

    @GetMapping("/api/v1/users/{email}")
    public ApiResponse<UserAndPostsFindResponseDto> findByEmail(@PathVariable String email) {
        return ApiResponse.<UserAndPostsFindResponseDto>builder()
                .success(true)
                .response(UserAndPostsFindResponseDto.builder()
                        .user(userService.findByEmail(email))
                        .build())
                .build();
    }

    @PutMapping("/api/v1/users/{email}")
    public ApiResponse<String> update(@PathVariable String email, @RequestBody UserUpdateRequestDto requestDto) {
        validateUpdateDto(requestDto);

        UserUpdateModel userUpdateModel = UserUpdateModel.builder()
                .email(email)
                .name(requestDto.getName())
                .password(requestDto.getPassword())
                .build();
        userService.updateUser(userUpdateModel);
        return ApiResponse.<String>builder()
                .success(true)
                .response("업데이트 성공하였습니다.")
                .build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ExceptionResponse IllegalArgumentExceptionHandler(Exception exception) {
        return ExceptionResponse.builder()
                .success(false)
                .message(exception.getMessage())
                .build();
    }

    private void validateUpdateDto(UserUpdateRequestDto requestDto) {
        if (!StringUtils.hasText(requestDto.getName())) {
            throw new IllegalArgumentException("이름이 비어있습니다.");
        }
        if (!StringUtils.hasText(requestDto.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 비어있습니다.");
        }
    }
}
