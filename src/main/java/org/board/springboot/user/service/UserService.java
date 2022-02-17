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
        return userRepository.save(userSaveRequestDto.toEntity()).getId();
    }

    @Transactional(readOnly = true)
    public UserFindResponseDto find(UserFindRequestDto userFindRequestDto) {
        User user = userRepository.findByEmailAndPassword(userFindRequestDto.getEmail(), userFindRequestDto.getPassword())
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다."));
        return new UserFindResponseDto(user);
    }
}
