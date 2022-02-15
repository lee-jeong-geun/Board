package org.board.springboot.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserFindRequestDto {
    private String email;
    private String password;

    @Builder
    public UserFindRequestDto(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
