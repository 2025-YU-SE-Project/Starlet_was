package com.example.starlet_be.domains.email.repository;

import com.example.starlet_be.domains.email.entity.Email;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailRepository extends JpaRepository<Email, Long> {
}