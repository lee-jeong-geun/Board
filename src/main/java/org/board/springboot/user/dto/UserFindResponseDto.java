package org.board.springboot.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.board.springboot.user.domain.User;

@Getter
@NoArgsConstructor
public class UserFindResponseDto {
    private String name;
    private String email;

    @Builder
    public UserFindResponseDto(User user) {
        this.name = user.getName();
        this.email = user.getEmail();
    }
}
