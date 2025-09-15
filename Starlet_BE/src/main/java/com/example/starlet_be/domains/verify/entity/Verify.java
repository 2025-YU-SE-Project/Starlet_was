package com.example.starlet_be.domains.verify.entity;

import com.example.starlet_be.domains.email.entity.Email;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
public class Verify {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String token;

    @Column(nullable = false)
    private VerifyType type;

    @Column
    private LocalDateTime expireTime;

    @OneToOne(mappedBy = "verify", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Email email;


    @Builder public Verify(String token, VerifyType type, LocalDateTime expireTime) {
        this.token = token;
        this.type = type;
        this.expireTime = expireTime;
    }

    public void updateStatus(String token, VerifyType type, LocalDateTime expireTime) {
        this.token = token;
        this.type = type;
        this.expireTime = expireTime;
    }
}
