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
                        .postsId(p.getId())
                        .title(p.getTitle())
                        .content(p.getContent())
                        .viewCount(p.getViewCount())
                        .userEmail(p.getUser().getEmail())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PostsFindResponseDto findById(Long id) {
        Posts posts = postsRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("해당 게시글이 없습니다."));
        return PostsFindResponseDto.builder()
                .postsId(posts.getId())
                .title(posts.getTitle())
                .content(posts.getContent())
                .viewCount(posts.getViewCount())
                .userEmail(posts.getUser().getEmail())
                .build();
    }

    @Transactional
    public int viewCountUpdateById(Long id, int updateCount) {
        Posts posts = postsRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new IllegalStateException("해당 게시글이 없습니다."));
        int viewCount = posts.getViewCount() + updateCount;
        posts.viewCountUpdate(viewCount);
        return viewCount;
    }
}
