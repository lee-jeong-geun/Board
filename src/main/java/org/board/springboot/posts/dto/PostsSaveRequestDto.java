package org.board.springboot.posts.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.board.springboot.posts.domain.Posts;
import org.board.springboot.user.domain.User;

@Getter
@NoArgsConstructor
public class PostsSaveRequestDto {

    private String title;
    private String content;
    private User user;

    @Builder
    public PostsSaveRequestDto(String title, String content, User user) {
        this.title = title;
        this.content = content;
        this.user = user;
    }

    public Posts toEntity() {
        return Posts.builder()
                .title(title)
                .content(content)
                .user(user)
                .build();
    }

}
