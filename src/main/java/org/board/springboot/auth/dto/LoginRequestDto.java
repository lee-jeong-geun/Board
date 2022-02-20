package org.board.springboot.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.board.springboot.user.dto.UserFindRequestDto;

@Getter
@NoArgsConstructor
public class LoginRequestDto {
    String email;
    String password;

    @Builder
    public LoginRequestDto(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public UserFindRequestDto toUserFindRequestDto() {
        return UserFindRequestDto.builder()
                .email(this.email)
                .password(this.password)
                .build();
    }
}
