package org.board.springboot.comment.controller;

import lombok.RequiredArgsConstructor;
import org.board.springboot.comment.dto.CommentSaveRequestDto;
import org.board.springboot.comment.service.CommentService;
import org.board.springboot.common.dto.ApiResponse;
import org.board.springboot.common.dto.ExceptionResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class CommentApiController {

    private final CommentService commentService;

    @PostMapping("/api/v1/comment")
    public ApiResponse<Long> commentSave(@RequestBody CommentSaveRequestDto commentSaveRequestDto) {
        Long id = commentService.save(commentSaveRequestDto);

        return ApiResponse.<Long>builder()
                .success(true)
                .response(id)
                .build();
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ExceptionResponse ExceptionHandler(Exception exception) {
        return ExceptionResponse.builder()
                .success(false)
                .message(exception.getMessage())
                .build();
    }
}
