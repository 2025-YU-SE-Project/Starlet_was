package com.example.starlet_be.domains.friend.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FriendListItemResDto {

    private Long id;

    private Long userId;
    private String nickname;
    private String profileUrl;

    private Long totalStars;
    private Long totalConstellations;

    private String level;

    public static FriendListItemResDto of(
            Long id,
            Long userId,
            String nickname,
            String profileUrl,
            Long totalStars,
            Long totalConstellations,
            String level
    ) {
        return FriendListItemResDto.builder()
                .id(id)
                .userId(userId)
                .nickname(nickname)
                .profileUrl(profileUrl)
                .totalStars(totalStars)
                .totalConstellations(totalConstellations)
                .level(level)
                .build();
    }

}
