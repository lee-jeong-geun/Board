package org.board.springboot.comment.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentSaveRequestBody {

    private String content;
    private Long postsId;

    @Builder
    public CommentSaveRequestBody(String content, Long postsId) {
        this.content = content;
        this.postsId = postsId;
    }
}
