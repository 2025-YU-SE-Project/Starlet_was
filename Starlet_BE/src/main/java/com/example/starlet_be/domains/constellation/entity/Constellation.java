package com.example.starlet_be.domains.constellation.entity;

import com.example.starlet_be.domains.connection.entity.Connection;
import com.example.starlet_be.domains.star.entity.Star;
import com.example.starlet_be.domains.user.entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
public class Constellation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    @Column(nullable = false)
    private LocalDate createAt;

    @Column
    private boolean isRepresentative;

    @Column(nullable = false)
    private Double x;

    @Column(nullable = false)
    private Double y;

    @OneToMany(mappedBy = "constellation", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Star> stars = new ArrayList<>();

    @OneToMany(mappedBy = "constellation", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Connection> connections = new ArrayList<>();


    @Builder public Constellation(User user, String name, String description, LocalDate createAt, boolean isRepresentative, Double x, Double y) {
        this.user = user;
        this.name = name;
        this.description = description;
        this.createAt = createAt;
        this.isRepresentative = isRepresentative;
        this.x = x;
        this.y = y;
    }

    public void changeRepresentative() {
        this.isRepresentative = !this.isRepresentative;
    }

}
