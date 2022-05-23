package org.board.springboot.comment.service;

import lombok.RequiredArgsConstructor;
import org.board.springboot.comment.domain.Comment;
import org.board.springboot.comment.domain.CommentRepository;
import org.board.springboot.comment.dto.CommentFindResponseDto;
import org.board.springboot.comment.dto.CommentSaveRequestDto;
import org.board.springboot.posts.domain.Posts;
import org.board.springboot.posts.domain.PostsRepository;
import org.board.springboot.user.domain.User;
import org.board.springboot.user.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserService userService;
    private final PostsRepository postsRepository;

    @Transactional
    public Long save(CommentSaveRequestDto commentSaveRequestDto) {
        User user = userService.findByEmail(commentSaveRequestDto.getUserEmail());
        Posts posts = postsRepository.findById(commentSaveRequestDto.getPostsId())
                .orElseThrow(() -> new IllegalStateException("해당 게시글이 없습니다."));
        Comment comment = Comment.builder()
                .content(commentSaveRequestDto.getContent())
                .user(user)
                .posts(posts)
                .build();
        return commentRepository.save(comment).getId();
    }

    @Transactional(readOnly = true)
    public List<CommentFindResponseDto> findByPostsId(Long postsId) {
        Posts posts = postsRepository.findById(postsId)
                .orElseThrow(() -> new IllegalStateException("해당 게시글이 없습니다."));
        return posts.getCommentList().stream()
                .map(comment -> CommentFindResponseDto.builder()
                        .content(comment.getContent())
                        .userEmail(comment.getUser().getEmail())
                        .build())
                .collect(Collectors.toList());
    }
}
