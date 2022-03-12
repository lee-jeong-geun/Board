package org.board.springboot.posts.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class PostsSaveRequestBody {

    private String title;
    private String content;

    @Builder
    public PostsSaveRequestBody(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
