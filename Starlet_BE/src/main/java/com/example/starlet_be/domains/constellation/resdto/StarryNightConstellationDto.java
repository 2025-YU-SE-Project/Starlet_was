package com.example.starlet_be.domains.constellation.resdto;

import com.example.starlet_be.domains.connection.resdto.StarryNightConnectionDto;
import com.example.starlet_be.domains.star.resdto.StarryNightStarDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class StarryNightConstellationDto {
    // 별자리에 대한 정보
    private Long constellationId;
    private Long userId;
    private Double x;
    private Double y;


    // 각 별에 대한 정보 리스트
    private List<StarryNightStarDto> stars;


    // 각 선에 대한 정보 리스트
    private List<StarryNightConnectionDto> connections;


    @Builder
    public StarryNightConstellationDto(Long constellationId, Long userId, Double x, Double y, List<StarryNightStarDto> stars, List<StarryNightConnectionDto> connections) {
        this.constellationId = constellationId;
        this.userId = userId;
        this.x = x;
        this.y = y;
        this.stars = stars;
        this.connections = connections;
    }
}
