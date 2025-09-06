package com.example.starlet_be.domains.verify.service;

import com.example.starlet_be.domains.email.entity.Email;
import com.example.starlet_be.domains.email.repository.EmailRepository;
import com.example.starlet_be.domains.email.reqdto.EmailAddressDto;
import com.example.starlet_be.domains.user.entity.User;
import com.example.starlet_be.domains.user.repository.UserRepository;
import com.example.starlet_be.domains.verify.entity.Verify;
import com.example.starlet_be.domains.verify.entity.VerifyType;
import com.example.starlet_be.domains.verify.repository.VerifyRepository;
import com.example.starlet_be.domains.verify.reqdto.PasswordResetConfirmDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VerifyService {

    private final VerifyRepository verifyRepository;
    private final EmailRepository emailRepository;
    private final UserRepository userRepository;

    // 토큰 랜덤 생성
    private String createToken(){
        return UUID.randomUUID().toString();
    }

    // 이메일 생성과 함께 인증 객체 생성
    @Transactional
    public Verify createVerify(){
        String token = createToken();
        LocalDateTime expireTime = LocalDateTime.now().plusHours(24);
        Verify verify = Verify.builder()
                .token(token)
                .type(VerifyType.EMAIL_VERIFICATION)
                .expireTime(expireTime)
                .build();
        return verifyRepository.save(verify);
    }

    // 토큰 인증
    @Transactional
    public Verify validateToken(String token, VerifyType type){
        Verify verify = verifyRepository.findByToken(token).orElseThrow(
                () -> new IllegalArgumentException("유효하지 않은 토큰")
        );
        // 기간이 지나면 알아서 삭제하도록 구현
        if(verify.getType() != type)
            throw new IllegalArgumentException("토큰 타입이 올바르지 않습니다.");
        return verify;
    }

    // 인증 상태 가져오기(관리자용)
    @Transactional(readOnly = true)
    public VerifyType getVerifyType(Email email){
        return email.getVerify().getType();
    }


    // 가입 인증 상태가 만료되면 이메일 객체와 인증 객체 삭제
    // 비밀번호 변경 요청이었다면 이거는 그냥 만료시킴



    // 비밀번호 변경 요청에 따라 상태 변환
    @Transactional
    public void passwordResetRequestStatus(Email email){

        Verify verify = email.getVerify();
        verify.setType(VerifyType.REQUEST_PASSWORD_RESET);
        verify.setToken(createToken());
        verify.setExpireTime(LocalDateTime.now().plusHours(24));
        verifyRepository.save(verify);
    }

    // 1. 가입 이메일 인증 받기
    @Transactional
    public void emailVerification(String token) {
        Verify verify = validateToken(token, VerifyType.EMAIL_VERIFICATION);
        verify.setType(VerifyType.VERIFY);
        verify.setToken(null);
        verify.setExpireTime(null);
        verifyRepository.save(verify);
    }


    // 2. 비밀번호 변경 허용 인증 이후 비밀번호 변경상태로 전환
    @Transactional
    public void passwordResetVerification(String token) {
        Verify verify = validateToken(token, VerifyType.REQUEST_PASSWORD_RESET);
        verify.setType(VerifyType.CHANGING_PASSWORD);
        verify.setToken(null);
        verify.setExpireTime(null);
        verifyRepository.save(verify);
    }


    // 3. 새로운 비밀번호 반영(새로운 비밀번호 생성)
    @Transactional
    public void updatePassword(PasswordResetConfirmDto dto) {
        // 이메일이 맞는지 확인하고 새 비밀번호 암호화하여 넣기
        Email email = emailRepository.findByAddress(dto.getEmail()).orElseThrow(
                () -> new IllegalArgumentException("이메일 없음")
        );

        if(email.getVerify().getType() != VerifyType.CHANGING_PASSWORD)
            throw new IllegalArgumentException("비밀번호 초기화중인 계정이 아닙니다.");

        User user = userRepository.findByEmail(dto.getEmail()).orElseThrow(
                () -> new IllegalArgumentException("이메일을 가진 유저가 없음")
        );

        user.setPassword(dto.getNewPassword());
        userRepository.save(user);

        Verify verify = email.getVerify();
        verify.setType(VerifyType.VERIFY);
        verifyRepository.save(verify);
    }

}
