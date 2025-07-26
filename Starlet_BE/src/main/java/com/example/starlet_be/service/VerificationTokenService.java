package com.example.starlet_be.service;

import com.example.starlet_be.entity.User;
import com.example.starlet_be.entity.VerificationToken;
import com.example.starlet_be.entity.enums.TokenType;
import com.example.starlet_be.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VerificationTokenService {

    private final VerificationTokenRepository verificationTokenRepository;

    // 이메일 인증 토큰 생성
    public VerificationToken createToken(User user, TokenType type){
        String token = UUID.randomUUID().toString();
        LocalDateTime expireTime = LocalDateTime.now().plusMinutes(15); // 15분이 지나면 만료
        VerificationToken verificationToken = VerificationToken.builder()
                .user(user)
                .token(token)
                .type(type)
                .expireTime(expireTime)
                .build();
        return verificationTokenRepository.save(verificationToken);
    }

    // 토큰 검증
    public User validateToken(String token, TokenType type){
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 토큰"));
        if(verificationToken.getExpireTime().isBefore(LocalDateTime.now()))
            throw new IllegalArgumentException("이미 만료된 토큰입니다.");
        if(verificationToken.getType() != type)
            throw new IllegalArgumentException("토큰 타입이 올바르지 않습니다.");
        return verificationToken.getUser();
    }


    // 만료된 토큰들 삭제
    // 이 부분들은 언제 동작 시켜야할지 고민입니다.
    public void deleteExpiredTokens(){
        verificationTokenRepository.deleteByExpireTimeBefore(LocalDateTime.now());
    }
}
