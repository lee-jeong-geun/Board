package org.board.springboot.comment.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentDeleteRequestDto {

    private Long commentId;
    private String userEmail;

    @Builder
    public CommentDeleteRequestDto(Long commentId, String userEmail) {
        this.commentId = commentId;
        this.userEmail = userEmail;
    }
}
