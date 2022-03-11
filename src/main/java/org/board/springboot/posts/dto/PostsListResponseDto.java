package org.board.springboot.posts.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class PostsListResponseDto {

    private boolean success;
    private List<PostsFindResponseDto> postsList;

    @Builder
    public PostsListResponseDto(boolean success, List<PostsFindResponseDto> postsList) {
        this.success = success;
        this.postsList = postsList;
    }
}
