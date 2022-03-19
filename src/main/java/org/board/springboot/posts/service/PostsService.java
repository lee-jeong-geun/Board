package org.board.springboot.posts.service;

import lombok.RequiredArgsConstructor;
import org.board.springboot.posts.domain.Posts;
import org.board.springboot.posts.domain.PostsRepository;
import org.board.springboot.posts.dto.PostsFindResponseDto;
import org.board.springboot.posts.dto.PostsSaveRequestDto;
import org.board.springboot.user.domain.User;
import org.board.springboot.user.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PostsService {

    private final PostsRepository postsRepository;
    private final UserService userService;

    @Transactional
    public Long save(PostsSaveRequestDto postsSaveRequestDto) {
        User user = userService.findByEmail(postsSaveRequestDto.getEmail());
        Posts posts = Posts.builder()
                .title(postsSaveRequestDto.getTitle())
                .content(postsSaveRequestDto.getContent())
                .user(user)
                .build();
        return postsRepository.save(posts).getId();
    }

    @Transactional(readOnly = true)
    public List<PostsFindResponseDto> findAll() {
        return postsRepository.findAll()
                .stream()
                .map(p -> PostsFindResponseDto.builder()
                        .title(p.getTitle())
                        .content(p.getContent())
                        .userId(p.getUser().getId())
                        .userName(p.getUser().getName())
                        .build())
                .collect(Collectors.toList());
    }
}
