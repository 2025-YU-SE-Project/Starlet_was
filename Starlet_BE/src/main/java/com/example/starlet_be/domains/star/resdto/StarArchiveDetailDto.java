package com.example.starlet_be.domains.star.resdto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class StarArchiveDetailDto {
    private Long starId;
    private Double x;
    private Double y;
    private String color;
    private LocalDate date;
}
