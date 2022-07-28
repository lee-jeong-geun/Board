package org.board.springboot.comment.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentFindResponseDto {
    private Long commentId;
    private String content;
    private String userEmail;
    private Long postsId;

    @Builder
    public CommentFindResponseDto(Long commentId, String content, String userEmail, Long postsId) {
        this.commentId = commentId;
        this.content = content;
        this.userEmail = userEmail;
        this.postsId = postsId;
    }
}
