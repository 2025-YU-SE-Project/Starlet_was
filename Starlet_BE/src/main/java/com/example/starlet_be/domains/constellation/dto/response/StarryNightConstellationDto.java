package com.example.starlet_be.domains.constellation.dto.response;

import com.example.starlet_be.domains.connection.dto.response.StarryNightConnectionDto;
import com.example.starlet_be.domains.star.dto.response.StarryNightStarDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class StarryNightConstellationDto {
    // 별자리에 대한 정보
    @Schema(example = "1")
    private Long constellationId;

    @Schema(example = "2")
    private Long userId;

    @Schema(example = "0.55")
    private Double x;

    @Schema(example = "0.33")
    private Double y;

    @Schema(example = "bottle con")
    private String name;

    @Schema(example = "2025-11-11")
    private LocalDate createAt;

    @Schema(example = "2025-09-01")
    private LocalDate belongDate;


    // 각 별에 대한 정보 리스트
    @Schema(example = "")
    private List<StarryNightStarDto> stars;


    // 각 선에 대한 정보 리스트
    @Schema(example = "")
    private List<StarryNightConnectionDto> connections;


    @Builder
    public StarryNightConstellationDto(
            Long constellationId, Long userId,
            Double x, Double y, String name,
            LocalDate createAt, LocalDate belongDate,
            List<StarryNightStarDto> stars,
            List<StarryNightConnectionDto> connections) {
        this.constellationId = constellationId;
        this.userId = userId;
        this.x = x;
        this.y = y;
        this.name = name;
        this.createAt = createAt;
        this.belongDate = belongDate;
        this.stars = stars;
        this.connections = connections;
    }
}
