package org.board.springboot.posts.controller;

import lombok.RequiredArgsConstructor;
import org.board.springboot.common.dto.ApiResponse;
import org.board.springboot.common.dto.ExceptionResponse;
import org.board.springboot.posts.dto.PostsFindResponseDto;
import org.board.springboot.posts.dto.PostsSaveRequestBody;
import org.board.springboot.posts.dto.PostsSaveRequestDto;
import org.board.springboot.posts.service.PostsService;
import org.board.springboot.user.service.UserSessionService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class PostsApiController {

    private final PostsService postsService;
    private final UserSessionService userSessionService;
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
        checkSessionStateByEmail(email);
        PostsSaveRequestDto postsSaveRequestDto = PostsSaveRequestDto.builder()
                .title(postsSaveRequestBody.getTitle())
                .content(postsSaveRequestBody.getContent())
                .email(email)
                .build();

        Long id = postsService.save(postsSaveRequestDto);
        updateSessionStateByEmail(email);
        return ApiResponse.<Long>builder()
                .success(true)
                .response(id)
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

    private void checkSessionStateByEmail(String email) {
        userSessionService.checkTodayRemainPostsCount(email);
    }

    private void updateSessionStateByEmail(String email) {
        userSessionService.updateTodayRemainPostsCount(email);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ExceptionResponse IllegalStateExceptionHandler(Exception exception) {
        return ExceptionResponse.builder()
                .success(false)
                .message(exception.getMessage())
                .build();
    }
}
