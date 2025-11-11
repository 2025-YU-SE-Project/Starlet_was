package com.example.starlet_be.domains.user.entity;

import com.example.starlet_be.domains.constellation.entity.Constellation;
import com.example.starlet_be.domains.diary.entity.Diary;
import com.example.starlet_be.domains.email.entity.Email;
import com.example.starlet_be.domains.star.entity.Star;
import com.example.starlet_be.domains.user.dto.UserResDto;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class User{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name = "email_id", nullable = false)
    private Email email;

    @Column
    private String profilePhotoUrl;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Star> stars = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Diary> diaries = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Constellation> constellations = new ArrayList<>();

    @Builder public User(String nickname, String password, Email email, String profilePhotoUrl) {
        this.nickname = nickname;
        this.password = password;
        this.email = email;
        this.profilePhotoUrl = profilePhotoUrl;
    }

    public UserResDto toResDto() {
        return UserResDto.builder().id(id).nickname(nickname).email(email.getAddress()).build();
    }

    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void changeProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
    }

    public void changeNickname(String nickname) { this.nickname = nickname; }
}
