package com.example.starlet_be.domains.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginInfoDto {

    @Schema(example = "1")
    private Long userId;

    @Schema(example = "starlet@example.com")
    private String email;

    @Schema(example = "starlet")
    private String nickname;

    @Schema(example = "618hdjfnvs3jr1f....")
    private String accessToken;

    @Schema(example = "1fmdivy283yfo1m....")
    private String refreshToken;

    @Builder public LoginInfoDto(Long userId, String email, String nickname, String accessToken, String refreshToken) {
        this.userId = userId;
        this.email = email;
        this.nickname = nickname;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
