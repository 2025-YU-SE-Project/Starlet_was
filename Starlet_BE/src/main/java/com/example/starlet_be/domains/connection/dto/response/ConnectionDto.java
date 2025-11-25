package com.example.starlet_be.domains.connection.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ConnectionDto {

    @Schema(example = "1")
    private Long startStarId;

    @Schema(example = "2")
    private Long endStarId;
}
