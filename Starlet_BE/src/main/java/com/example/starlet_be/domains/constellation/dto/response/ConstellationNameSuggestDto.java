package com.example.starlet_be.domains.constellation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ConstellationNameSuggestDto {

    @Schema(example = "bottle con")
    private String name;

    @Schema(example = "this is bottle con")
    private String description;
}
