package org.board.springboot.user.service;

import lombok.RequiredArgsConstructor;
import org.board.springboot.user.domain.User;
import org.board.springboot.user.domain.UserRepository;
import org.board.springboot.user.dto.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final Clock clock;

    @Transactional
    public Long save(UserSaveRequestDto userSaveRequestDto) {
        validateDuplicateUser(userSaveRequestDto);

        return userRepository.save(userSaveRequestDto.toEntity()).getId();
    }

    private void validateDuplicateUser(UserSaveRequestDto userSaveRequestDto) {
        userRepository.findByEmail(userSaveRequestDto.getEmail()).ifPresent(m -> {
            throw new IllegalArgumentException("해당 유저가 이미 존재합니다.");
        });
    }

    @Transactional(readOnly = true)
    public UserFindResponseDto findByEmailAndPassword(UserFindRequestDto userFindRequestDto) {
        User user = userRepository.findByEmailAndPassword(userFindRequestDto.getEmail(), userFindRequestDto.getPassword())
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다."));
        return UserFindResponseDto.builder()
                .user(user)
                .build();
    }

    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다."));
    }

    @Transactional(readOnly = true)
    public List<UserFindPostsListResponseDto> findPostsByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다."))
                .getPostsList()
                .stream()
                .map(p -> UserFindPostsListResponseDto.builder()
                        .posts(p)
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateLastLoginTime(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다."));

        user.updateLastLogIn(LocalDateTime.now(clock));
    }

    @Transactional
    public void updateUser(UserUpdateModel userUpdateModel) {
        User user = userRepository.findByEmail(userUpdateModel.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다."));

        user.update(userUpdateModel.getName(), userUpdateModel.getEmail(), userUpdateModel.getPassword());
    }
}
