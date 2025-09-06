package com.example.starlet_be.domains.verify.repository;

import com.example.starlet_be.domains.verify.entity.Verify;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerifyRepository extends JpaRepository<Verify, Long> {
}