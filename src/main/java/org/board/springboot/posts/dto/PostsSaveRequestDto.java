package org.board.springboot.posts.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostsSaveRequestDto {

    private String title;
    private String content;
    private String email;

    @Builder
    public PostsSaveRequestDto(String title, String content, String email) {
        this.title = title;
        this.content = content;
        this.email = email;
    }
}
