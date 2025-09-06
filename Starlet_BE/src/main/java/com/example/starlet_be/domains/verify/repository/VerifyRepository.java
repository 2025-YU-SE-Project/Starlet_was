package com.example.starlet_be.domains.verify.repository;

import com.example.starlet_be.domains.verify.entity.Verify;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerifyRepository extends JpaRepository<Verify, Long> {
    Optional<Verify> findByToken(String token);
}