package com.example.starlet_be.domains.connection.connection;

import com.example.starlet_be.domains.constellation.entity.Constellation;
import com.example.starlet_be.domains.star.entity.Star;
import com.example.starlet_be.domains.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class Connection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "constellation_id", nullable = false)
    private Constellation constellation;

    @ManyToOne
    @JoinColumn(name = "start_star_id", nullable = false)
    private Star start;

    @ManyToOne
    @JoinColumn(name = "end_star_id", nullable = false)
    private Star end;
}
