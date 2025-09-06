package com.example.starlet_be.domains.diary.entity;

import com.example.starlet_be.domains.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
public class Diary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Emotion emotion;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private List<Factor> factors = new ArrayList<>();
    // 필드에서 바로 리스트 할 필요 있을지도

    @Column
    private String content;

    @Column(nullable = false)
    private LocalDate createAt;

    @Builder public Diary(User user, Emotion emotion, List<Factor> factors, String content, LocalDate createAt) {
        this.user = user;
        this.emotion = emotion;
        this.factors = factors;
        this.content = content;
        this.createAt = createAt;
    }
}
