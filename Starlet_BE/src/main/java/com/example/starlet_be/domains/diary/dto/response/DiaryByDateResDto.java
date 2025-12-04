package com.example.starlet_be.domains.diary.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DiaryByDateResDto {

    @Schema(example = "true")
    private boolean hasDiary;

    @Schema(example = "2025-12-04")
    private LocalDate date;

    private DiaryResDto diary;

    public static DiaryByDateResDto empty(LocalDate date) {
        return DiaryByDateResDto.builder()
                .hasDiary(false)
                .date(date)
                .diary(null)
                .build();
    }

    public static DiaryByDateResDto of(com.example.starlet_be.domains.diary.entity.Diary diary) {
        return DiaryByDateResDto.builder()
                .hasDiary(true)
                .date(diary.getCreateAt())
                .diary(DiaryResDto.of(diary))
                .build();
    }
}
