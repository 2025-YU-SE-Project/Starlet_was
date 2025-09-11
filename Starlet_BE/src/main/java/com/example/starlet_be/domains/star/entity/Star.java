package com.example.starlet_be.domains.star.entity;

import com.example.starlet_be.domains.connection.connection.Connection;
import com.example.starlet_be.domains.constellation.entity.Constellation;
import com.example.starlet_be.domains.diary.entity.Color;
import com.example.starlet_be.domains.diary.entity.Diary;
import com.example.starlet_be.domains.user.entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
public class Star {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Color color;

    @Column(nullable = false)
    private Double x;

    @Column(nullable = false)
    private Double y;

    @ManyToOne
    @JoinColumn(name = "constellation_id")
    private Constellation constellation;

    @OneToOne
    @JoinColumn(name = "diary_id")
    private Diary diary;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Star.java에 추가 (이 방법은 복잡성이 증가하여 특별한 이유가 없다면 추천하지 않습니다)
    @OneToMany(mappedBy = "start", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Connection> connectionsAsStart = new ArrayList<>();

    @OneToMany(mappedBy = "end", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Connection> connectionsAsEnd = new ArrayList<>();

}
