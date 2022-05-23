package org.board.springboot.comment.controller;

import lombok.RequiredArgsConstructor;
import org.board.springboot.auth.service.AuthService;
import org.board.springboot.auth.service.JWTService;
import org.board.springboot.comment.dto.CommentFindResponseDto;
import org.board.springboot.comment.dto.CommentSaveRequestBody;
import org.board.springboot.comment.dto.CommentSaveRequestDto;
import org.board.springboot.comment.service.CommentService;
import org.board.springboot.common.dto.ApiResponse;
import org.board.springboot.common.dto.ExceptionResponse;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static org.board.springboot.util.CookieUtils.getCookie;

@RequiredArgsConstructor
@RestController
public class CommentApiController {

    private final CommentService commentService;
    private final AuthService authService;
    private final JWTService jwtService;
    private final HttpServletRequest httpServletRequest;

    @PostMapping("/api/v1/comment")
    public ApiResponse<Long> commentSave(@RequestBody CommentSaveRequestBody commentSaveRequestBody) {
        validateLoginState();

        Cookie tokenCookie = getCookie(httpServletRequest.getCookies(), "token");
        String userEmail = jwtService.getEmail(tokenCookie.getValue());

        CommentSaveRequestDto commentSaveRequestDto = CommentSaveRequestDto.builder()
                .content(commentSaveRequestBody.getContent())
                .userEmail(userEmail)
                .postsId(commentSaveRequestBody.getPostsId())
                .build();
        Long id = commentService.save(commentSaveRequestDto);

        return ApiResponse.<Long>builder()
                .success(true)
                .response(id)
                .build();
    }

    @GetMapping("/api/v1/comment/{postsId}")
    public ApiResponse<List<CommentFindResponseDto>> getComments(@PathVariable Long postsId) {
        List<CommentFindResponseDto> commentList = commentService.findByPostsId(postsId);

        return ApiResponse.<List<CommentFindResponseDto>>builder()
                .success(true)
                .response(commentList)
                .build();
    }


    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ExceptionResponse ExceptionHandler(Exception exception) {
        return ExceptionResponse.builder()
                .success(false)
                .message(exception.getMessage())
                .build();
    }

    private void validateLoginState() {
        if (!authService.isLoggedIn()) {
            throw new IllegalStateException("로그인 상태가 아닙니다.");
        }
    }
}
