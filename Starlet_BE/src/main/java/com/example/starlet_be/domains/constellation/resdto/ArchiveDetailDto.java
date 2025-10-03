package com.example.starlet_be.domains.constellation.resdto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class ArchiveDetailDto {
    private Long constellationId;
    private String name;
    private String description;
    private LocalDate date;
    private Boolean isRepresentative;
    private Integer happyness;

}
