package com.example.starlet_be.domains.star.resdto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StarInfoDto {
    private Long starId;
    private Long userId;
    private Long constellationId;
    private Long diaryId;

    @Builder
    public StarInfoDto(Long starId, Long userId, Long constellationId, Long diaryId) {
        this.starId = starId;
        this.userId = userId;
        this.constellationId = constellationId;
        this.diaryId = diaryId;
    }
}
