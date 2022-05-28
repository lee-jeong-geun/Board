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

    @Builder
    public CommentFindResponseDto(Long commentId, String content, String userEmail) {
        this.commentId = commentId;
        this.content = content;
        this.userEmail = userEmail;
    }
}
