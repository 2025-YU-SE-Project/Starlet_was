package com.example.starlet_be.domains.star.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class StarsIdDto {
    @Schema(description = "별 고유 ID들", example = "[1, 2, 3, 4]")
    private List<Long> starIds;
}
