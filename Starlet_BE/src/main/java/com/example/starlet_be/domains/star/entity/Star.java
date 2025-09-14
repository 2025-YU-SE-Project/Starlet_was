package com.example.starlet_be.domains.star.entity;

import com.example.starlet_be.domains.connection.entity.Connection;
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
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Data
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

    @OneToMany(mappedBy = "start", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Connection> connectionsAsStart = new ArrayList<>();

    @OneToMany(mappedBy = "end", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Connection> connectionsAsEnd = new ArrayList<>();

    @Builder
    public Star(Color color, Double x, Double y, Constellation constellation, Diary diary, User user) {
        this.color = color;
        this.x = x;
        this.y = y;
        this.constellation = constellation;
        this.diary = diary;
        this.user = user;
    }

    public void joinConstellation(Constellation constellation) {
        this.constellation = constellation;
    }

    public void changePosition(Double x, Double y) {
        this.x = x;
        this.y = y;
    }

}
