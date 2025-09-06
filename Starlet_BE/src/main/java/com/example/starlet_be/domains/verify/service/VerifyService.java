package com.example.starlet_be.domains.verify.service;

import com.example.starlet_be.domains.email.entity.Email;
import com.example.starlet_be.domains.verify.entity.Verify;
import com.example.starlet_be.domains.verify.entity.VerifyType;
import com.example.starlet_be.domains.verify.repository.VerifyRepository;
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

    // 이메일 생성과 함께 인증 객체 생성
    @Transactional
    public Verify createVerify(VerifyType type){
        String token = UUID.randomUUID().toString();
        LocalDateTime expireTime = LocalDateTime.now().plusHours(24);
        Verify verify = Verify.builder()
                .token(token)
                .type(type)
                .expireTime(expireTime)
                .build();
        return verifyRepository.save(verify);
    }

    // 토큰 인증
    @Transactional
    public Email validateToken(String token, VerifyType type){
        Verify verify = verifyRepository.findByToken(token).orElseThrow(
                () -> new IllegalArgumentException("유효하지 않은 토큰")
        );
        // 기간이 지나면 알아서 삭제하도록 구현
        if(verify.getType() != type)
            throw new IllegalArgumentException("토큰 타입이 올바르지 않습니다.");
        return verify.getEmail();
    }

    // 인증 상태 가져오기


    // 인증 상태가 만료되면 이메일 객체와 인증 객체 삭제


    // 3. 계정생성 이메일 인증 이후 인증된 계정으로 전환
//    @Transactional
//    public void emailVerification(String token){
//
//    }



    // 4. 비밀번호 초기화 이메일 인증 이후 비밀번호 변경상태로 전환



    // 5. 비밀번호 변경 승인 요청



    // 6. 새로운 비밀번호 반영

}
