package com.example.starlet_be.domains.diary.dto.response;

import com.example.starlet_be.domains.diary.entity.Color;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class StarMonthlyResDto {

    @Schema(example = "2025-09-03")
    private LocalDate date;

    @Schema(example = "ORANGE")
    private Color color;
}
