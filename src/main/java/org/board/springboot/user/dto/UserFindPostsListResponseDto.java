package org.board.springboot.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.board.springboot.posts.domain.Posts;

@NoArgsConstructor
@Getter
public class UserFindPostsListResponseDto {

    private Long id;
    private String title;
    private String content;

    @Builder
    public UserFindPostsListResponseDto(Posts posts) {
        this.id = posts.getId();
        this.title = posts.getTitle();
        this.content = posts.getContent();
    }
}
