package org.board.springboot.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginUserResponseDto {
    private String name;
    private String email;

    @Builder
    public LoginUserResponseDto(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
