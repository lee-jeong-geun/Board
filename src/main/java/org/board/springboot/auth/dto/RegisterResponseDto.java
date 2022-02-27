package org.board.springboot.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RegisterResponseDto {
    private boolean success;
    private Long id;

    @Builder
    public RegisterResponseDto(boolean success, Long id) {
        this.success = success;
        this.id = id;
    }
}
