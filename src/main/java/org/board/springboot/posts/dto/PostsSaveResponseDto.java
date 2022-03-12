package org.board.springboot.posts.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostsSaveResponseDto {

    private boolean success;
    private long id;

    @Builder
    public PostsSaveResponseDto(boolean success, long id) {
        this.success = success;
        this.id = id;
    }
}
