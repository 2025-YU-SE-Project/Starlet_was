package com.example.starlet_be.domains.constellation.repository;

import com.example.starlet_be.domains.constellation.entity.Constellation;
import com.example.starlet_be.domains.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ConstellationRepository extends JpaRepository<Constellation, Long> {
    List<Constellation> findByUserAndCreateAtBetween(User user, LocalDate startDate, LocalDate endDate);
}