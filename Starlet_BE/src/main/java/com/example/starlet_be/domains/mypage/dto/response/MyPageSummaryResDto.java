package com.example.starlet_be.domains.mypage.dto.response;

import com.example.starlet_be.domains.constellation.dto.response.StarryNightConstellationDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class MyPageSummaryResDto {

    private final UserSummaryResDto profile;

    private final LevelResDto level;

    private final StarryNightConstellationDto representativeConstellation;

    private final List<MonthlyCountResDto> monthlyConstellationCounts;

    private final List<EmotionCountResDto> monthlyEmotionCounts;

    @Builder
    private MyPageSummaryResDto(
            UserSummaryResDto profile,
            LevelResDto level,
            StarryNightConstellationDto representativeConstellation,
            List<MonthlyCountResDto> monthlyConstellationCounts,
            List<EmotionCountResDto> monthlyEmotionCounts
    ) {
        this.profile = profile;
        this.level = level;
        this.representativeConstellation = representativeConstellation;
        this.monthlyConstellationCounts = monthlyConstellationCounts;
        this.monthlyEmotionCounts = monthlyEmotionCounts;
    }

    public static MyPageSummaryResDto of(
            UserSummaryResDto profile,
            LevelResDto level,
            StarryNightConstellationDto rep,
            List<MonthlyCountResDto> monthlyCounts,
            List<EmotionCountResDto> emotionCounts
    ) {
        return MyPageSummaryResDto.builder()
                .profile(profile)
                .level(level)
                .representativeConstellation(rep)
                .monthlyConstellationCounts(monthlyCounts)
                .monthlyEmotionCounts(emotionCounts)
                .build();
    }
}
