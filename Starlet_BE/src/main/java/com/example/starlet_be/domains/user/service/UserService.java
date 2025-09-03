package com.example.starlet_be.domains.user.service;

import com.example.starlet_be.domains.user.reqdto.UserReqDto;
import com.example.starlet_be.domains.user.resdto.UserResDto;
import com.example.starlet_be.domains.user.entity.User;
import com.example.starlet_be.domains.user.repository.UserRepository;
import com.example.starlet_be.exception.CustomException;
import com.example.starlet_be.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 유저 단일 조회
    @Transactional(readOnly = true)
    public UserResDto getUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );
        return user.toResDto();
    }

    // 유저 전체 조회
    @Transactional(readOnly = true)
    public List<UserResDto> getUserList() {
        List<User> users = userRepository.findAll();
        List<UserResDto> dtos = new ArrayList<>();

        for(User user : users)
            dtos.add(user.toResDto());

        return dtos;
    }

    // 회원가입
    @Transactional
    public User signUp(UserReqDto dto) {
        // 1. 입력정보 유효성 확인, dto와 컨트롤러 계층에서 처리 가능
//        if(dto.getNickname().isBlank()
//                || !dto.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
//                || dto.getPassword().isBlank())
//            return false;

        // 2. 닉네임 및 이메일 중복 확인
        if(existEmail(dto.getEmail()) || existNickname(dto.getNickname()))
            throw new CustomException(ErrorCode.DUPLICATE_INFO_CONFLICT);

        // 3. 비밀번호 형식 확인, 일단은 제한하지 않음.

        // 4. 엔티티로 변환 후 저장, 암호화작업

        return userRepository.save(dto.toEntity(passwordEncoder.encode(dto.getPassword())));
    }

    // 이메일 존재(중복) 확인
    @Transactional(readOnly = true)
    public boolean existEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    // 닉네임 존재(중복) 확인
    @Transactional(readOnly = true)
    public boolean existNickname(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    // 로그인 되어있는 유저가 계정 삭제
    @Transactional
    public void deleteCurrentUser(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND));
        userRepository.delete(user);
    }

    // 이메일 기반 찾기
    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow( () -> new CustomException(ErrorCode.USER_NOT_FOUND) );
    }

    // 아래 로직들은 authService 클래스로 이동

//    // 비밀번호 변경 승인 요청
//    public void requestNewPassword(PasswordResetReqDto dto){
//        User user = findByEmail(dto.getEmail());
//        Token token = tokenService.createToken(user, TokenType.PASSWORD_RESET);
//        authService.sendPasswordResetEmail(user, token.getToken());
//    }

//    // 새로운 비밀번호 반영
//    @Transactional
//    public void updatePassword(PasswordResetConfirmDto dto){
//        User user = tokenService.validateToken(dto.getToken(), TokenType.PASSWORD_RESET);
//        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
//        userRepository.save(user);
//        tokenService.deleteTokenByUser(user);
//    }
}
