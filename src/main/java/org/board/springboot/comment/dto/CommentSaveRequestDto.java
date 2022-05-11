package org.board.springboot.comment.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentSaveRequestDto {

    private String content;
    private String userEmail;
    private Long postsId;

    @Builder
    public CommentSaveRequestDto(String content, String userEmail, Long postsId) {
        this.content = content;
        this.userEmail = userEmail;
        this.postsId = postsId;
    }
}
