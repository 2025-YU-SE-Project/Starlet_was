package com.example.starlet_be.domains.star.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StarryNightStarDto {
    private Long starId;
    private Long userId;
    private String color;
    private String date;
    private Double x;
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
