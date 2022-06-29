package org.board.springboot.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.board.springboot.comment.dto.CommentFindResponseDto;
import org.board.springboot.posts.dto.PostsFindResponseDto;
import org.board.springboot.user.domain.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class UserAndPostsFindResponseDto {

    private String name;
    private String email;
    private LocalDateTime lastLogIn;
    private List<PostsFindResponseDto> posts;
    private List<CommentFindResponseDto> comment;

    @Builder
    public UserAndPostsFindResponseDto(User user) {
        this.name = user.getName();
        this.email = user.getEmail();
        this.lastLogIn = user.getLastLogIn();
        this.posts = user.getPostsList().stream()
                .map(p -> PostsFindResponseDto.builder()
                        .postsId(p.getId())
                        .title(p.getTitle())
                        .content(p.getContent())
                        .build())
                .collect(Collectors.toList());
        this.comment = user.getCommentList().stream()
                .map(c -> CommentFindResponseDto.builder()
                        .commentId(c.getId())
                        .content(c.getContent())
                        .postsId(c.getPosts().getId())
                        .build())
                .collect(Collectors.toList());
    }
}
