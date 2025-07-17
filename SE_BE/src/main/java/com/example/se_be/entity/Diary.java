package com.example.se_be.entity;

import com.example.se_be.entity.enums.Emotion;
import com.example.se_be.entity.enums.Factor;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
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
    private List<Factor> factors;
    // 필드에서 바로 리스트 할 필요 있을지도

    @Column
    private String content;

    @Column(nullable = false)
    private LocalDate createAt;
}
