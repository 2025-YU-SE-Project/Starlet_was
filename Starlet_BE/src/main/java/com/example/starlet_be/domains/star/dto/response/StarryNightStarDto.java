package com.example.starlet_be.domains.star.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StarryNightStarDto {

    @Schema(example = "1")
    private Long starId;

    @Schema(example = "2")
    private Long userId;

    @Schema(example = "BLUE")
    private String color;

    @Schema(example = "2025-11-11")
    private String date;

    @Schema(example = "0.55")
    private Double x;

    @Schema(example = "0.66")
    private Double y;

    @Builder public StarryNightStarDto(Long starId, Long userId, String color, String date, Double x, Double y) {
        this.starId = starId;
        this.userId = userId;
        this.color = color;
        this.date = date;
        this.x = x;
        this.y = y;
    }
}
