package com.example.starlet_be.domains.user.entity;

import com.example.starlet_be.domains.user.resdto.UserResDto;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private Boolean verified;

    @Builder public User(String nickname, String password, String email) {
        this.nickname = nickname;
        this.password = password;
        this.email = email;
        this.verified = false;
    }

    public UserResDto toResDto() {
        return UserResDto.builder().id(id).nickname(nickname).email(email).build();
    }
}
