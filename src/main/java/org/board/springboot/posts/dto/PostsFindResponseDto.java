package org.board.springboot.posts.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class PostsFindResponseDto {

    private String title;
    private String content;
    private String userEmail;

    @Builder
    public PostsFindResponseDto(String title, String content, String userEmail) {
        this.title = title;
        this.content = content;
        this.userEmail = userEmail;
    }
}
