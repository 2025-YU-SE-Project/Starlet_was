package com.example.starlet_be.domains.mypage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class EmotionCountResDto {
    @Schema(example = "HAPPINESS")
    private String emotion;

    @Schema(example = "2")
    private long count;
}
