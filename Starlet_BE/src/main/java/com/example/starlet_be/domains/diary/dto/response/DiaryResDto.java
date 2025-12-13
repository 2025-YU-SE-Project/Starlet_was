package com.example.starlet_be.domains.diary.dto.response;

import com.example.starlet_be.domains.diary.entity.Color;
import com.example.starlet_be.domains.diary.entity.Diary;
import com.example.starlet_be.domains.diary.entity.Emotion;
import com.example.starlet_be.domains.diary.entity.Factor;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class DiaryResDto {

    @Schema(example = "2025-09-09")
    private LocalDate date;

    @Schema(example = "HAPPINESS")
    private Emotion emotion;

    @Schema(example = "YELLOW")
    private Color color;

    @Schema(example = "[\"FRIEND\",\"WORK\"]")
    private List<Factor> factors;

    @Schema(example = "오늘은 소공 수업을 했는데, 너무너무 재미있었다!")
    private String content;

    public static DiaryResDto of(Diary d) {
        return DiaryResDto.builder()
                .date(d.getCreateAt())
                .emotion(d.getEmotion())
                .color(d.getEmotion().getColor())
                .factors(d.getFactors())
                .content(d.getContent())
                .build();
    }
}
