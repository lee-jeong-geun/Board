package org.board.springboot.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LogoutResponseDto {
    private boolean success;

    @Builder
    public LogoutResponseDto(boolean success) {
        this.success = success;
    }
}
