package org.board.springboot.comment.service;

import lombok.RequiredArgsConstructor;
import org.board.springboot.comment.domain.CommentRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

}
