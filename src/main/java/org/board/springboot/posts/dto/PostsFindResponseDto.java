package org.board.springboot.posts.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class PostsFindResponseDto {

    private Long postsId;
    private String title;
    private String content;
    private String userEmail;

    @Builder
    public PostsFindResponseDto(Long postsId, String title, String content, String userEmail) {
        this.postsId = postsId;
        this.title = title;
        this.content = content;
        this.userEmail = userEmail;
    }
}
