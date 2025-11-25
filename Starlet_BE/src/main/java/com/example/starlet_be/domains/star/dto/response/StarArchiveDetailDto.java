package com.example.starlet_be.domains.star.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class StarArchiveDetailDto {

    @Schema(example = "1")
    private Long starId;

    @Schema(example = "0.55")
    private Double x;

    @Schema(example = "0.66")
    private Double y;

    @Schema(example = "BLUE")
    private String color;

    @Schema(example = "2025-11-11")
    private LocalDate date;
}
