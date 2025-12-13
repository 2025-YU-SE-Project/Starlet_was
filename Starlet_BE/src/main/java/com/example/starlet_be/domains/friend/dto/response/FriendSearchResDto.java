package com.example.starlet_be.domains.friend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class FriendSearchResDto {
    private String nickname;
    private String profileUrl;

    @Schema(
            description = "친구 상태 -> 가능한 값: PENDING(대기), ACCEPTED(친구됨), NONE(관계 없음)"
    )
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
