package com.example.starlet_be.repository;

import com.example.starlet_be.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    // 토큰 문자열로 조회
    Optional<VerificationToken> findByToken(String token);

    // 만료된 토큰 삭제
    void deleteByExpireTimeBefore(LocalDateTime now);

}
