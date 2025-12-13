package com.example.starlet_be.domains.friend.entity;

import io.swagger.v3.oas.annotations.media.Schema;

public enum FriendStatus {

    @Schema(description = "친구 요청 대기중")
    PENDING,

    @Schema(description = "친구 관계 수락됨")
    ACCEPTED,

    @Schema(description = "관계 없음")
    NONE
}
