package com.example.starlet_be.domains.constellation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ConstellationPositionDto {

    @Schema(description = "x 좌표", example = "0.55")
    @NotNull(message = "별의 x좌표를 입력해주세요.")
    private Double x;

    @Schema(description = "y 좌표", example = "0.66")
    @NotNull(message = "별의 y좌표를 입력해주세요.")
    private Double y;
}
