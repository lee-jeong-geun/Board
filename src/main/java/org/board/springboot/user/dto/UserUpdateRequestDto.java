package org.board.springboot.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserUpdateRequestDto {
    private String name;
    private String password;

    @Builder
    public UserUpdateRequestDto(String name, String password) {
        this.name = name;
        this.password = password;
    }
}
