package org.board.springboot.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserUpdateModel {
    private String email;
    private String name;
    private String password;

    @Builder
    public UserUpdateModel(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
    }
}
