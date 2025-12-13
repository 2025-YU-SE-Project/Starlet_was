package com.example.starlet_be.domains.verify.repository;

import com.example.starlet_be.domains.verify.entity.Verify;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VerifyRepository extends JpaRepository<Verify, Long> {
    Optional<Verify> findByToken(String token);

    List<Verify> findAllByExpireTimeBefore(LocalDateTime now);

    Optional<Verify> findByEmail_Address(String emailAddress);
}