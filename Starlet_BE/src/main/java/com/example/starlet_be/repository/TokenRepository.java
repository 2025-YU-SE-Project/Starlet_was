package com.example.starlet_be.repository;

import com.example.starlet_be.entity.Token;
import com.example.starlet_be.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    // 토큰 문자열로 조회
    Optional<Token> findByToken(String token);

    // 만료된 토큰 삭제
    void deleteByExpireTimeBefore(LocalDateTime now);

    void deleteByUserId(Long userId);
}
