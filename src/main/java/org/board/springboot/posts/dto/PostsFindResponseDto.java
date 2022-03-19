package org.board.springboot.posts.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class PostsFindResponseDto {

    private String title;
    private String content;
    private Long userId;
    private String userName;

    @Builder
    public PostsFindResponseDto(String title, String content, Long userId, String userName) {
        this.title = title;
        this.content = content;
        this.userId = userId;
        this.userName = userName;
    }
}
