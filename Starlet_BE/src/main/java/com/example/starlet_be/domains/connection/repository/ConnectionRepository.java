package com.example.starlet_be.domains.connection.repository;

import com.example.starlet_be.domains.connection.entity.Connection;
import com.example.starlet_be.domains.constellation.entity.Constellation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConnectionRepository extends JpaRepository<Connection, Long> {
    List<Connection> findByConstellation(Constellation constellation);
}