package org.board.springboot.posts.controller;

import lombok.RequiredArgsConstructor;
import org.board.springboot.common.dto.ApiResponse;
import org.board.springboot.common.dto.ExceptionResponse;
import org.board.springboot.posts.dto.PostsFindResponseDto;
import org.board.springboot.posts.dto.PostsSaveRequestBody;
import org.board.springboot.posts.dto.PostsSaveRequestDto;
import org.board.springboot.posts.service.PostsService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class PostsApiController {

    private final PostsService postsService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final HttpServletRequest httpServletRequest;
    private static final int TODAY_POSTS_COUNT_MAX = 10;

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
        checkTodayRemainPostsCount(email);
    }

    private void checkTodayRemainPostsCount(String email) {
        if (!redisTemplate.opsForHash().hasKey(email, "todayRemainPostsCount")) {
            return;
        }
        int remainPostsCount = Integer.parseInt(redisTemplate.opsForHash().get(email, "todayRemainPostsCount").toString());
        if (remainPostsCount <= 0) {
            throw new IllegalStateException("오늘은 더이상 게시글을 올릴 수 없습니다.");
        }
    }

    private void updateSessionStateByEmail(String email) {
        updateTodayRemainPostsCount(email);
    }

    private void updateTodayRemainPostsCount(String email) {
        if (!redisTemplate.opsForHash().hasKey(email, "todayRemainPostsCount")) {
            redisTemplate.opsForHash().put(email, "todayRemainPostsCount", String.valueOf(TODAY_POSTS_COUNT_MAX));
        }
        int remainPostsCount = Integer.parseInt(redisTemplate.opsForHash().get(email, "todayRemainPostsCount").toString()) - 1;
        redisTemplate.opsForHash().put(email, "todayRemainPostsCount", String.valueOf(remainPostsCount));
    }


    @ExceptionHandler(IllegalStateException.class)
    public ExceptionResponse IllegalStateExceptionHandler(Exception exception) {
        return ExceptionResponse.builder()
                .success(false)
                .message(exception.getMessage())
                .build();
    }
}
