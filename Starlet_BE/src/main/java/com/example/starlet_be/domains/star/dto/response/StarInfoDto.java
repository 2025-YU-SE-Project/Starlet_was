package com.example.starlet_be.domains.star.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StarInfoDto {

    @Schema(example = "1")
    private Long starId;

    @Schema(example = "2")
    private Long userId;

    @Schema(example = "3")
    private Long diaryId;

    @Builder
    public StarInfoDto(Long starId, Long userId, Long diaryId) {
        this.starId = starId;
        this.userId = userId;
        this.diaryId = diaryId;
    }
}
