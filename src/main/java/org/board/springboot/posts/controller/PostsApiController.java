package org.board.springboot.posts.controller;

import lombok.RequiredArgsConstructor;
import org.board.springboot.auth.service.AuthService;
import org.board.springboot.auth.service.JWTService;
import org.board.springboot.common.dto.ApiResponse;
import org.board.springboot.common.dto.ExceptionResponse;
import org.board.springboot.posts.dto.PostsFindResponseDto;
import org.board.springboot.posts.dto.PostsSaveRequestBody;
import org.board.springboot.posts.dto.PostsSaveRequestDto;
import org.board.springboot.posts.service.PostsService;
import org.board.springboot.redis.user.UserSessionService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class PostsApiController {

    private final PostsService postsService;
    private final UserSessionService userSessionService;
    private final AuthService authService;
    private final JWTService jwtService;
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
        validateLoginState();

        Cookie tokenCookie = getCookie(httpServletRequest.getCookies(), "token");
        String email = jwtService.getEmail(tokenCookie.getValue());

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

    @DeleteMapping("/api/v1/posts/{id}")
    public ApiResponse<Long> postsDelete(@PathVariable Long id) {
        return ApiResponse.<Long>builder()
                .success(true)
                .response(postsService.delete(id))
                .build();
    }

    @GetMapping("/api/v1/posts/{id}")
    public ApiResponse<PostsFindResponseDto> findById(@PathVariable Long id) {
        postsService.viewCountUpdateById(id, 1);

        return ApiResponse.<PostsFindResponseDto>builder()
                .success(true)
                .response(postsService.findById(id))
                .build();
    }

    private void validateLoginState() {
        if (!authService.isLoggedIn()) {
            throw new IllegalStateException("로그인 상태가 아닙니다.");
        }
    }

    private void checkSessionStateByEmail(String email) {
        userSessionService.checkTodayRemainPostsCountUpdate(email);
        userSessionService.checkTodayRemainPostsCount(email);
        userSessionService.checkLastPostsSaveTime(email);
    }

    private void updateSessionStateByEmail(String email) {
        userSessionService.updateTodayRemainPostsCount(email);
        userSessionService.updateLastPostsSaveTime(email);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ExceptionResponse IllegalStateExceptionHandler(Exception exception) {
        return ExceptionResponse.builder()
                .success(false)
                .message(exception.getMessage())
                .build();
    }

    private Cookie getCookie(Cookie[] cookies, String name) {
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)) {
                return cookie;
            }
        }
        return null;
    }
}
