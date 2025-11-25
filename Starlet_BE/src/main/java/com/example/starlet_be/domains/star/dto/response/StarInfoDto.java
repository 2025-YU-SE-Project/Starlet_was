package com.example.starlet_be.domains.star.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StarInfoDto {
    private Long starId;
    private Long userId;
    private Long diaryId;

    @Builder
    public StarInfoDto(Long starId, Long userId, Long diaryId) {
        this.starId = starId;
        this.userId = userId;
        this.diaryId = diaryId;
    }
}
