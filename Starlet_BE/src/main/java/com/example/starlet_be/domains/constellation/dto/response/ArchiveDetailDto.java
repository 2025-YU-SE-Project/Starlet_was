package com.example.starlet_be.domains.constellation.dto.response;

import com.example.starlet_be.domains.connection.dto.response.ConnectionDto;
import com.example.starlet_be.domains.star.dto.response.StarArchiveDetailDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class ArchiveDetailDto {

    @Schema(example = "1")
    private Long constellationId;

    @Schema(example = "bottle con")
    private String name;

    @Schema(example = "this is bottle con")
    private String description;

    @Schema(example = "2025-11-11")
    private LocalDate date;

    @Schema(example = "true")
    private Boolean isRepresentative;

    @Schema(example = "StarArchiveDetailDto")
    private List<StarArchiveDetailDto> stars;

    @Schema(example = "")
    private List<ConnectionDto> connections;

    @Schema(example = "1")
    private Integer happynessCount;

    @Schema(example = "2")
    private Integer funnyCount;

    @Schema(example = "3")
    private Integer neutralCount;

    @Schema(example = "4")
    private Integer surprisingCount;

    @Schema(example = "5")
    private Integer angerCount;

    @Schema(example = "6")
    private Integer sadnessCount;

}
