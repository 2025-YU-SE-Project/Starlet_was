package com.example.starlet_be.domains.connection.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class StarryNightConnectionDto {
    private Long connectionId;
    private Long startStarId;
    private Long endStarId;

    @Builder
    public StarryNightConnectionDto(Long connectionId, Long startStarId, Long endStarId) {
        this.connectionId = connectionId;
        this.startStarId = startStarId;
        this.endStarId = endStarId;
    }
}
