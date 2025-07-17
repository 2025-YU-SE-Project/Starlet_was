package com.example.se_be.entity;

import com.example.se_be.entity.enums.Color;
import jakarta.persistence.*;

@Entity
public class Star {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne
    @JoinColumn(name = "diary_id")
    private Diary diary;

    @ManyToOne
    @JoinColumn(name = "constellation_id")
    private Constellation constellation;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Color color;

    @Column
    private Double x;

    @Column
    private Double y;

    @Column
    private Boolean isLeader;
}
