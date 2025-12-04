package com.example.starlet_be.domains.diary.dto.response;

import com.example.starlet_be.domains.diary.entity.Diary;

import java.time.LocalDate;

public record DiaryByDateResDto(
        boolean hasDiary,
        LocalDate date,
        DiaryResDto diary
) {
    public static DiaryByDateResDto empty(LocalDate date) {
        return new DiaryByDateResDto(false, date, null);
    }

    public static DiaryByDateResDto of(Diary diary) {
        return new DiaryByDateResDto(true, diary.getCreateAt(), DiaryResDto.of(diary));
    }
}
