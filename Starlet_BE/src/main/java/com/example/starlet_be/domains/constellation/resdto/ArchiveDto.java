package com.example.starlet_be.domains.constellation.resdto;

import com.example.starlet_be.domains.connection.reqdto.ConnectionDto;
import com.example.starlet_be.domains.star.resdto.StarArchiveDto;
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
