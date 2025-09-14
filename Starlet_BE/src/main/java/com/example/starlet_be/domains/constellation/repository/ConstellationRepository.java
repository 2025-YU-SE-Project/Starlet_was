package com.example.starlet_be.domains.constellation.repository;

import com.example.starlet_be.domains.constellation.entity.Constellation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConstellationRepository extends JpaRepository<Constellation, Long> {
}