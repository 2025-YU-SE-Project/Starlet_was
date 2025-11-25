package com.example.starlet_be.domains.constellation.dto.response;

import com.example.starlet_be.domains.connection.dto.response.ConnectionDto;
import com.example.starlet_be.domains.star.dto.response.StarArchiveDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class ArchiveDto {

    @Schema(example = "1")
    private Long constellationId;

    @Schema(example = "bottle con")
    private String name;

    @Schema(example = "this is bottle con")
    private String description;

    @Schema(example = "2025-11-11")
    private LocalDate date;

    @Schema(example = "false")
    private Boolean isRepresentative;

    @Schema(example = "")
    private List<StarArchiveDto> stars;

    @Schema(example = "")
    private List<ConnectionDto> connections;
}
