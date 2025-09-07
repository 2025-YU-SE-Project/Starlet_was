package com.example.starlet_be.domains.user.repository;

import com.example.starlet_be.domains.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmailAddress(String email);
    boolean existsByNickname(String nickname);

    Optional<User> findByEmailAddress(String email);
}
