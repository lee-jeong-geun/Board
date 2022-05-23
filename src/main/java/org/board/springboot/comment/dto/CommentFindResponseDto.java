package org.board.springboot.comment.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentFindResponseDto {

    public String content;
    public String userEmail;

    @Builder
    public CommentFindResponseDto(String content, String userEmail) {
        this.content = content;
        this.userEmail = userEmail;
    }
}
