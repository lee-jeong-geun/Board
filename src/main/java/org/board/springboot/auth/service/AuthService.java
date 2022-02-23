package org.board.springboot.auth.service;

import lombok.RequiredArgsConstructor;
import org.board.springboot.auth.dto.LoginRequestDto;
import org.board.springboot.auth.dto.LoginResponseDto;
import org.board.springboot.auth.dto.LoginUserResponseDto;
import org.board.springboot.user.dto.UserFindResponseDto;
import org.board.springboot.user.service.UserService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final UserService userService;

    public LoginUserResponseDto getLoginUserResponseDto(UserFindResponseDto userFindResponseDto) {
        if (userFindResponseDto == null) {
            return null;
        }
        return LoginUserResponseDto.builder()
                .name(userFindResponseDto.getName())
                .email(userFindResponseDto.getEmail())
                .build();
    }

    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        UserFindResponseDto userFindResponseDto = userService.find(loginRequestDto.toUserFindRequestDto());
        boolean success = userFindResponseDto != null;
        LoginUserResponseDto loginUserResponseDto = getLoginUserResponseDto(userFindResponseDto);

        return LoginResponseDto.builder()
                .success(success)
                .user(loginUserResponseDto)
                .build();
    }
}
