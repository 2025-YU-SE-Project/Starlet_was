package com.example.starlet_be.domains.constellation.dto;

import com.example.starlet_be.domains.connection.dto.ConnectionDto;
import com.example.starlet_be.domains.star.dto.response.StarArchiveDetailDto;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class ArchiveDetailDto {
    private Long constellationId;
    private String name;
    private String description;
    private LocalDate date;
    private Boolean isRepresentative;
    private List<StarArchiveDetailDto> stars;
    private List<ConnectionDto> connections;
    private Integer happynessCount;
    private Integer funnyCount;
    private Integer neutralCount;
    private Integer surprisingCount;
    private Integer angerCount;
    private Integer sadnessCount;
}
