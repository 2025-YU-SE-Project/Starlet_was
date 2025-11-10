package com.example.starlet_be.domains.constellation.repository;

import com.example.starlet_be.domains.constellation.entity.Constellation;
import com.example.starlet_be.domains.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ConstellationRepository extends JpaRepository<Constellation, Long> {
    List<Constellation> findByUserAndBelongDateBetween(User user, LocalDate startDate, LocalDate endDate);
    List<Constellation> findByUser(User user);

    Optional<Constellation> findByUserAndIsRepresentative(User user, boolean isRepresentative);

    // 총 별자리 수
    long countByUser(User user);

    List<Constellation> findAllByUserAndCreateAtBetween(User user, LocalDate start, LocalDate end);

}