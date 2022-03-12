package org.board.springboot.posts.controller;

import lombok.RequiredArgsConstructor;
import org.board.springboot.posts.dto.PostsFindResponseDto;
import org.board.springboot.posts.dto.PostsListResponseDto;
import org.board.springboot.posts.service.PostsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class PostsApiController {

    private final PostsService postsService;

    @GetMapping("/api/v1/posts")
    public PostsListResponseDto postsList() {
        boolean success = true;
        List<PostsFindResponseDto> result = postsService.findAll();
        return PostsListResponseDto.builder()
                .success(success)
                .postsList(result)
                .build();
    }
}
