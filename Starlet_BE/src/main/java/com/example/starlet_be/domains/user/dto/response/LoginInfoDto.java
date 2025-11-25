package com.example.starlet_be.domains.user.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginInfoDto {
    private Long userId;
    private String email;
    private String nickname;
    private String accessToken;
    private String refreshToken;

    @Builder public LoginInfoDto(Long userId, String email, String nickname, String accessToken, String refreshToken) {
        this.userId = userId;
        this.email = email;
        this.nickname = nickname;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
