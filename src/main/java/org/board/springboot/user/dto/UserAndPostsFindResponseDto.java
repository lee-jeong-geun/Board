package org.board.springboot.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.board.springboot.posts.dto.PostsFindResponseDto;
import org.board.springboot.user.domain.User;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class UserAndPostsFindResponseDto {

    private String name;
    private String email;
    private List<PostsFindResponseDto> posts;

    @Builder
    public UserAndPostsFindResponseDto(User user) {
        this.name = user.getName();
        this.email = user.getEmail();
        this.posts = user.getPostsList().stream()
                .map(p -> PostsFindResponseDto.builder()
                        .postsId(p.getId())
                        .title(p.getTitle())
                        .content(p.getContent())
                        .build())
                .collect(Collectors.toList());
    }

}