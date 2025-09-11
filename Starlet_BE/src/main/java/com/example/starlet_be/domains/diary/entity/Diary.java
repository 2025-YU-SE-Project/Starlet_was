package com.example.starlet_be.domains.diary.entity;

import com.example.starlet_be.domains.star.entity.Star;
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

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "diary_factor",
            joinColumns = @JoinColumn(name = "diary_id")
    )
    @Column(name = "factor", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private List<Factor> factors = new ArrayList<>();

    @Column
    private String content;

    @Column(nullable = false)
    private LocalDate createAt;

    @OneToOne(mappedBy = "diary", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Star star;

    @Builder public Diary(User user, Emotion emotion, List<Factor> factors, String content, LocalDate createAt) {
        this.user = user;
        this.emotion = emotion;
        this.factors = factors;
        this.content = content;
        this.createAt = createAt;
    }

    @Version
    private Long version;

    public void updateContent(String newContent) {
        this.content = newContent;
    }

}
