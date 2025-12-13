package com.example.starlet_be.domains.email.repository;

import com.example.starlet_be.domains.email.entity.Email;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailRepository extends JpaRepository<Email, Long> {
    Optional<Email> findByAddress(String email);

    boolean existsByAddress(String address);
}