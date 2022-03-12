package org.board.springboot.user.service;

import lombok.RequiredArgsConstructor;
import org.board.springboot.user.domain.User;
import org.board.springboot.user.domain.UserRepository;
import org.board.springboot.user.dto.UserFindRequestDto;
import org.board.springboot.user.dto.UserFindResponseDto;
import org.board.springboot.user.dto.UserSaveRequestDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

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
    public UserFindResponseDto find(UserFindRequestDto userFindRequestDto) {
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
}
