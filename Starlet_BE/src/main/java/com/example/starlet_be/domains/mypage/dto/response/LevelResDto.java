package com.example.starlet_be.domains.mypage.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LevelResDto{

    @Schema(example="STARLIGHT_EXPLORER")
    private String code;

    @Schema(example = "별빛 탐험가")
    private String name;

    @Schema(example = "0")
    private Integer min;

    @Schema(example = "9")
    private Integer max;

    @Schema(example = "2")
    private Integer progressToNext;

}




