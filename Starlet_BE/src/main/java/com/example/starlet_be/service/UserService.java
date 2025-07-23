package com.example.starlet_be.service;

import com.example.starlet_be.dto.UserReqDto;
import com.example.starlet_be.dto.UserResDto;
import com.example.starlet_be.entity.User;
import com.example.starlet_be.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 유저 단일 조회
    public UserResDto getUser(Long id) {
        User user = userRepository.findById(id).orElse(null);
        return (user != null) ? user.toResDto() : null;
    }

    // 유저 전체 조회
    public List<UserResDto> getUserList() {
        List<User> users = userRepository.findAll();
        List<UserResDto> dtos = new ArrayList<>();

        for(User user : users)
            dtos.add(user.toResDto());

        return dtos;
    }

    // 회원가입
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

        // 4. 엔티티로 변환 후 저장, 암호화작업
        User user = userRepository.save(dto.toEntity(passwordEncoder.encode(dto.getPassword())));

        return user.getId();
    }

    // 이메일 존재(중복) 확인
    public boolean existEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    // 닉네임 존재(중복) 확인
    public boolean existNickname(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    public void deleteCurrentUser(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("해당하는 이메일을 가진 유저를 발견할 수 없음"));
        userRepository.delete(user);
    }
}
