package com.example.starlet_be.service;

import com.example.starlet_be.dto.UserReqDto;
import com.example.starlet_be.entity.User;
import com.example.starlet_be.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Long signUp(UserReqDto dto) {
        // 1. 입력정보 유효성 확인, dto와 컨트롤러 계층에서 처리 가능
//        if(dto.getNickname().isBlank()
//                || !dto.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
//                || dto.getPassword().isBlank())
//            return false;

        // 2. 닉네임 및 이메일 중복 확인
        if(existEmail(dto.getEmail()) || existNickname(dto.getNickname()))
            return null;

        // 3. 비밀번호 형식 확인, 일단은 제한하지 않음.

        // 4. 엔티티로 변환 후 저장
        User user = userRepository.save(dto.toEntity());

        return user.getId();
    }

    public boolean existEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean existNickname(String nickname) {
        return userRepository.existsByNickname(nickname);
    }
}
