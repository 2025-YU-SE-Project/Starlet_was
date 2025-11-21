package com.example.starlet_be.domains.friend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class FriendSearchResDto {
    private String nickname;
    private String profileUrl;

    private String status;

    private Long remainingSeconds;

    public static FriendSearchResDto of(
            String nickname,
            String profileUrl,
            String status,
            Long remainingSeconds
    ) {
        return FriendSearchResDto.builder()
                .nickname(nickname)
                .profileUrl(profileUrl)
                .status(status)
                .remainingSeconds(remainingSeconds)
                .build();
    }

}
