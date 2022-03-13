package org.board.springboot.posts.controller;

import lombok.RequiredArgsConstructor;
import org.board.springboot.common.dto.ExceptionResponse;
import org.board.springboot.posts.dto.*;
import org.board.springboot.posts.service.PostsService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class PostsApiController {

    private final PostsService postsService;
    private final HttpServletRequest httpServletRequest;

    @GetMapping("/api/v1/posts")
    public PostsListResponseDto postsList() {
        boolean success = true;
        List<PostsFindResponseDto> result = postsService.findAll();
        return PostsListResponseDto.builder()
                .success(success)
                .postsList(result)
                .build();
    }

    @PostMapping("/api/v1/posts")
    public PostsSaveResponseDto postsSave(@RequestBody PostsSaveRequestBody postsSaveRequestBody) {
        HttpSession httpSession = httpServletRequest.getSession();

        validateLoginState(httpSession);

        String email = httpSession.getAttribute("login").toString();

        PostsSaveRequestDto postsSaveRequestDto = PostsSaveRequestDto.builder()
                .title(postsSaveRequestBody.getTitle())
                .content(postsSaveRequestBody.getContent())
                .email(email)
                .build();
        long id = postsService.save(postsSaveRequestDto);
        return PostsSaveResponseDto.builder()
                .success(true)
                .id(id)
                .build();
    }

    private void validateLoginState(HttpSession httpSession) {
        if (httpSession.getAttribute("login") == null) {
            throw new IllegalStateException("로그인 상태가 아닙니다.");
        }
    }

    @ExceptionHandler(IllegalStateException.class)
    public ExceptionResponse IllegalStateExceptionHandler(Exception exception) {
        return ExceptionResponse.builder()
                .success(false)
                .message(exception.getMessage())
                .build();
    }
}
