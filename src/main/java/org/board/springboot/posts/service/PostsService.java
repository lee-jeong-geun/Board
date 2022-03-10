package org.board.springboot.posts.service;

import lombok.RequiredArgsConstructor;
import org.board.springboot.posts.domain.PostsRepository;
import org.board.springboot.posts.dto.PostsFindResponseDto;
import org.board.springboot.posts.dto.PostsSaveRequestDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PostsService {

    private final PostsRepository postsRepository;

    @Transactional
    public Long save(PostsSaveRequestDto postsSaveRequestDto) {
        return postsRepository.save(postsSaveRequestDto.toEntity()).getId();
    }

    @Transactional(readOnly = true)
    public List<PostsFindResponseDto> findAll() {
        return postsRepository.findAll()
                .stream()
                .map(p -> PostsFindResponseDto.builder()
                        .title(p.getTitle())
                        .content(p.getContent())
                        .userName(p.getUser().getName())
                        .build())
                .collect(Collectors.toList());
    }
}
