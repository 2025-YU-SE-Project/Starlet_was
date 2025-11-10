package com.example.starlet_be.domains.mypage.dto;

import com.example.starlet_be.domains.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserSummaryResDto{

    @Schema(example = "닉네임")
    private String nickname;

    @Schema(example = "9")
    private int totalStars;

    @Schema(example = "1")
    private int totalConstellations;

    public static UserSummaryResDto of(User user, long totalStars, long totalConstellations) {
        return UserSummaryResDto.builder()
                .nickname(user.getNickname())
                .totalStars((int) totalStars)
                .totalConstellations((int) totalConstellations)
                .build();
    }

}