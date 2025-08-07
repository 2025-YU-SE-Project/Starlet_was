package com.example.starlet_be.service;

import com.example.starlet_be.entity.User;
import com.example.starlet_be.entity.Token;
import com.example.starlet_be.entity.enums.TokenType;
import com.example.starlet_be.repository.TokenRepository;
import com.example.starlet_be.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;

    // 이메일 인증 토큰 생성
    public Token createToken(User user, TokenType type){
        String token = UUID.randomUUID().toString();
        LocalDateTime expireTime = LocalDateTime.now().plusHours(24); // 하루 유효시간
        Token verificationToken = Token.builder()
                .user(user)
                .token(token)
                .type(type)
                .expireTime(expireTime)
                .build();
        return tokenRepository.save(verificationToken);
    }

//    // 토큰 검증
//    public User validateToken(String token, TokenType type){
//        Token verificationToken = tokenRepository.findByToken(token)
//                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 토큰"));
//        if(verificationToken.getExpireTime().isBefore(LocalDateTime.now()))
//            throw new IllegalArgumentException("이미 만료된 토큰입니다.");
//        if(verificationToken.getType() != type)
//            throw new IllegalArgumentException("토큰 타입이 올바르지 않습니다.");
//        return verificationToken.getUser();
//    }

    // 토큰 검증
    public User validateToken(String token, TokenType type){
        Token verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 토큰"));
        if(verificationToken.getExpireTime().isBefore(LocalDateTime.now()))
            throw new IllegalArgumentException("이미 만료된 토큰입니다.");
        if(verificationToken.getType() != type)
            throw new IllegalArgumentException("토큰 타입이 올바르지 않습니다.");
        return verificationToken.getUser();
    }

    public boolean existTokenByUser(User user, TokenType type){
        try{
            Token token = tokenRepository.findByUserIdAndType(user.getId(), type).orElseThrow(
                    () -> new IllegalArgumentException("타입에 맞는 유저의 토큰이 존재하지 않음"));
            if(token.getExpireTime().isBefore(LocalDateTime.now()))
                throw new IllegalArgumentException("이미 만료된 토큰입니다.");
        } catch(IllegalArgumentException e){
            return false;
        }
        return true;
    }


    // 만료된 토큰들 삭제
    // 이 부분들은 언제 동작 시켜야할지 고민입니다.
    @Transactional
    public void deleteExpiredTokens(){
        tokenRepository.deleteByExpireTimeBefore(LocalDateTime.now());
    }

    @Transactional
    public void deleteTokenByUser(User user){
        tokenRepository.deleteByUserId(user.getId());
    }
}
