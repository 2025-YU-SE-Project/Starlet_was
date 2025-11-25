package com.example.starlet_be.domains.star.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StarArchiveDto {

    @Schema(example = "1")
    private Long starId;

    @Schema(example = "0.55")
    private Double x;

    @Schema(example = "0.66")
    private Double y;

    @Schema(example = "BLUE")
    private String color;
}
