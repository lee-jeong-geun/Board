package org.board.springboot.user.service;

import lombok.RequiredArgsConstructor;
import org.board.springboot.user.domain.UserRepository;
import org.board.springboot.user.dto.UserSaveRequestDto;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    public Long save(UserSaveRequestDto userSaveRequestDto) {
        return userRepository.save(userSaveRequestDto.toEntity()).getId();
    }
}
