package org.board.springboot.common.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ApiResponse<T> {

    private boolean success;
    private T response;

    @Builder
    public ApiResponse(boolean success, T response) {
        this.success = success;
        this.response = response;
    }
}
