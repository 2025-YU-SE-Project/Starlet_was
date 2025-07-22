package com.example.starlet_be.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Constellation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDate createAt;
}
