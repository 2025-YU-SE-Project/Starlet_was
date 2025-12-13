package com.example.starlet_be.domains.diary.dto.request;

import com.example.starlet_be.domains.diary.entity.Emotion;
import com.example.starlet_be.domains.diary.entity.Factor;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
public class DiaryCreateReqDto {

    @Schema(description = "감정", example = "HAPPINESS")
    @NotNull(message = "감정은 필수 입력입니다.")
    private Emotion emotion;

    @Schema(description = "감정 요인 태그 목록", example = "[\"FRIEND\",\"WORK\"]")
    @NotEmpty(message = "감정 요인은 최소 1개 이상 선택해야 합니다.")
    private List<@NotNull Factor> factors;

    @Schema(description = "내용(15~300자)", example = "오늘은 소공 수업을 했는데, 너무너무 재미있었다!")
    @Size(min = 15, max = 300, message = "내용은 15자 이상 300자 이하로 입력해주세요.")
    private String content;

    @Schema(description = "일기 날짜(오늘)", example = "2025-09-09")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @NotNull(message = "날짜는 필수 입력입니다.")
    private LocalDate date;
}
