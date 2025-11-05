package com.example.starlet_be.domains.constellation.dto;

import com.example.starlet_be.domains.connection.dto.ConnectionDto;
import com.example.starlet_be.domains.star.dto.StarArchiveDto;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class ArchiveDto {
    private Long constellationId;
    private String name;
    private String description;
    private LocalDate date;
    private Boolean isRepresentative;
    private List<StarArchiveDto> stars;
    private List<ConnectionDto> connections;
}
