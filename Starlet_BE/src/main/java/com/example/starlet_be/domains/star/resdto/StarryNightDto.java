package com.example.starlet_be.domains.star.resdto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StarryNightDto {
    private Long starId;
    private Long userId;
    private Long constellationId;
    private String color;
    private String date;
    private Double x;
    private Double y;

    @Builder public StarryNightDto(Long starId, Long userId, Long constellationId, String color, String date, Double x, Double y) {
        this.starId = starId;
        this.userId = userId;
        this.constellationId = constellationId;
        this.color = color;
        this.date = date;
        this.x = x;
        this.y = y;
    }
}
