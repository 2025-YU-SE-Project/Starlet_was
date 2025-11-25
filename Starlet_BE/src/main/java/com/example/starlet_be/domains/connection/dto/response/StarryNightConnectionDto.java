package com.example.starlet_be.domains.connection.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
public class StarryNightConnectionDto {


    @Schema(example = "1")
    private Long connectionId;

    @Schema(example = "2")
    private Long startStarId;

    @Schema(example = "3")
    private Long endStarId;

    @Builder
    public StarryNightConnectionDto(Long connectionId, Long startStarId, Long endStarId) {
        this.connectionId = connectionId;
        this.startStarId = startStarId;
        this.endStarId = endStarId;
    }
}
