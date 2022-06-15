package org.board.springboot.comment.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentFindResponseDto {

    public Long commentId;
    public String content;
    public String userEmail;
    public Long postsId;

    @Builder
    public CommentFindResponseDto(Long commentId, String content, String userEmail, Long postsId) {
        this.commentId = commentId;
        this.content = content;
        this.userEmail = userEmail;
        this.postsId = postsId;
    }
}
