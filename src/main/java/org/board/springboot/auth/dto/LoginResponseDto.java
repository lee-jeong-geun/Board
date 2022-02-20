package org.board.springboot.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginResponseDto {
    private boolean success;
    private LoginUserResponseDto user;

    @Builder
    public LoginResponseDto(boolean success, LoginUserResponseDto user) {
        this.success = success;
        this.user = user;
    }
}
