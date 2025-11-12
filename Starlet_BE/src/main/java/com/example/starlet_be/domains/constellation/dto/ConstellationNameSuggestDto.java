package com.example.starlet_be.domains.constellation.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ConstellationNameSuggestDto {
    private String name;
    private String description;
}
