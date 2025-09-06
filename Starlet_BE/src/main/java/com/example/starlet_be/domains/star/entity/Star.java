package com.example.starlet_be.domains.star.entity;

import com.example.starlet_be.domains.constellation.entity.Constellation;
import com.example.starlet_be.domains.diary.entity.Diary;
import com.example.starlet_be.domains.user.entity.User;
import com.example.starlet_be.domains.email.entity.Color;
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
