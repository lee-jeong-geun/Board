package org.board.springboot.posts.controller;

import lombok.RequiredArgsConstructor;
import org.board.springboot.common.dto.ApiResponse;
import org.board.springboot.common.dto.ExceptionResponse;
import org.board.springboot.posts.dto.PostsFindResponseDto;
import org.board.springboot.posts.dto.PostsSaveRequestBody;
import org.board.springboot.posts.dto.PostsSaveRequestDto;
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
    public ApiResponse<List<PostsFindResponseDto>> postsList() {
        return ApiResponse.<List<PostsFindResponseDto>>builder()
                .success(true)
                .response(postsService.findAll())
                .build();
    }

    @PostMapping("/api/v1/posts")
    public ApiResponse<Long> postsSave(@RequestBody PostsSaveRequestBody postsSaveRequestBody) {
        HttpSession httpSession = httpServletRequest.getSession();

        validateLoginState(httpSession);

        String email = httpSession.getAttribute("login").toString();

        PostsSaveRequestDto postsSaveRequestDto = PostsSaveRequestDto.builder()
                .title(postsSaveRequestBody.getTitle())
                .content(postsSaveRequestBody.getContent())
                .email(email)
                .build();
        return ApiResponse.<Long>builder()
                .success(true)
                .response(postsService.save(postsSaveRequestDto))
                .build();
    }

    @GetMapping("/api/v1/posts/{id}")
    public ApiResponse<PostsFindResponseDto> findById(@PathVariable Long id) {
        return ApiResponse.<PostsFindResponseDto>builder()
                .success(true)
                .response(postsService.findById(id))
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
