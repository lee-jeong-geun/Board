package org.board.springboot.auth.controller;

import lombok.RequiredArgsConstructor;
import org.board.springboot.auth.dto.RegisterRequestDto;
import org.board.springboot.user.dto.UserSaveRequestDto;
import org.board.springboot.user.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AuthApiController {

    private final UserService userService;

    @PostMapping("/api/v1/auth/register")
    public Long register(@RequestBody RegisterRequestDto requestDto) {
        UserSaveRequestDto userSaveRequestDto = UserSaveRequestDto.builder()
                .name(requestDto.getName())
                .email(requestDto.getEmail())
                .password(requestDto.getPassword())
                .build();

        return userService.save(userSaveRequestDto);
    }
}
