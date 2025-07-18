package com.example.se_be.repository;

import com.example.se_be.entity.Constellation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConstellationRepository extends JpaRepository<Constellation, Long> {
}
