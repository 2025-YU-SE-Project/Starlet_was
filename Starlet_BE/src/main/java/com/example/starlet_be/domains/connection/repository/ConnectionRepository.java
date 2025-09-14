package com.example.starlet_be.domains.connection.repository;

import com.example.starlet_be.domains.connection.entity.Connection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConnectionRepository extends JpaRepository<Connection, Long> {
}