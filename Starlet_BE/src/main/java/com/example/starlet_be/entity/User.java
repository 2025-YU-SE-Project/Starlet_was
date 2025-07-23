package com.example.starlet_be.entity;

import com.example.starlet_be.dto.UserResDto;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class User{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    @Builder public User(String nickname, String password, String email) {
        this.nickname = nickname;
        this.password = password;
        this.email = email;
    }

    public UserResDto toResDto() {
        return UserResDto.builder().id(id).nickname(nickname).email(email).build();
    }
}
