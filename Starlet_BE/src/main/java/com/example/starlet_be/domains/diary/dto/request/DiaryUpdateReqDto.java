package com.example.starlet_be.domains.diary.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class DiaryUpdateReqDto {

    @Schema(description = "수정 대상 날짜", example = "2025-09-09")
    @NotNull(message = "수정 대상 날짜는 필수입니다.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate date;

    @Schema(description = "내용(15~300자)", example = "사실은 수업 오기 너무너무 귀찮았다...흑흑")
    @Size(min = 15, max = 300, message = "내용은 15자 이상 300자 이하로 입력해주세요.")
    private String content;
}
