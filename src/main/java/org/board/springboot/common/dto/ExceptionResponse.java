package org.board.springboot.common.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ExceptionResponse {
    private boolean success;
    private String message;

    @Builder
    public ExceptionResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
