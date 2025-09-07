package com.example.starlet_be.domains.user.entity;

import com.example.starlet_be.domains.email.entity.Email;
import com.example.starlet_be.domains.user.resdto.UserResDto;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Data
public class User{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @OneToOne
    @JoinColumn(name = "email_id", nullable = false)
    private Email email;

    @Builder public User(String nickname, String password, Email email) {
        this.nickname = nickname;
        this.password = password;
        this.email = email;
    }

    public UserResDto toResDto() {
        return UserResDto.builder().id(id).nickname(nickname).email(email.getAddress()).build();
    }
}
