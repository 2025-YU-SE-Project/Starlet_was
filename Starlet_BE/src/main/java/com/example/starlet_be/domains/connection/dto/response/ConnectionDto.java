package com.example.starlet_be.domains.connection.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ConnectionDto {
    private Long startStarId;
    private Long endStarId;
}
