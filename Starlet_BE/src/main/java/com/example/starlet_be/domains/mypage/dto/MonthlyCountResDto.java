package com.example.starlet_be.domains.mypage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MonthlyCountResDto {

    @Schema(example = "10")
    private int month;

    @Schema(example = "1")
    private long count;
}
