package com.example.starlet_be.domains.friend.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FriendReqItemResDto {

    private Long id;

    private String nickname;
    private String profileUrl;

    private Long remainingSeconds;

    private String dDayLabel;

    public static FriendReqItemResDto of(
            Long id,
            String nickname,
            String profileUrl,
            Long remainingSeconds,
            String dDayLabel
    ) {
        return FriendReqItemResDto.builder()
                .id(id)
                .nickname(nickname)
                .profileUrl(profileUrl)
                .remainingSeconds(remainingSeconds)
                .dDayLabel(dDayLabel)
                .build();
    }
}
