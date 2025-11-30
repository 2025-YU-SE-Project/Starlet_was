package com.example.starlet_be.domains.mypage.dto.response;

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

    @Schema(example = "https://...")
    private String profilePhotoUrl;

    @Schema(example = "10")
    private long friendsCount;

    public static UserSummaryResDto of(User user, long totalStars, long totalConstellations, String profilePhotoUrl, long friendsCount) {
        return UserSummaryResDto.builder()
                .nickname(user.getNickname())
                .totalStars((int) totalStars)
                .totalConstellations((int) totalConstellations)
                .profilePhotoUrl(profilePhotoUrl)
                .friendsCount(friendsCount)
                .build();
    }

}