package com.example.starlet_be.domains.mypage.level;

import com.example.starlet_be.domains.mypage.dto.response.LevelResDto;

import java.util.List;

public final class LevelPolicy {

    private record Rule(int min, Integer max, String code, String name) {}

    private static final List<Rule> RULES = List.of(
            new Rule(0,   9,   "STARLIGHT_EXPLORER",       "별빛 탐험가"),
            new Rule(10,  29,  "STARCLUSTER_EXPLORER",     "별무리 탐험가"),
            new Rule(30,  59,  "CONSTELLATION_EXPLORER",   "별자리 탐험가"),
            new Rule(60,  99,  "NEBULA_EXPLORER",          "성운 탐험가"),
            new Rule(100, 149, "GALAXY_EXPLORER",          "은하 탐험가"),
            new Rule(150, 209, "GALAXY_CLUSTER_EXPLORER",  "은하단 탐험가"),
            new Rule(210, 299, "MILKYWAY_EXPLORER",        "은하수 탐험가"),
            new Rule(300, null,"UNIVERSE_EXPLORER",        "우주 탐험가")
    );

    private LevelPolicy() {}

    public static LevelResDto resolve(long totalStars) {
        for (Rule r : RULES) {
            boolean geMin = totalStars >= r.min;
            boolean leMax = (r.max == null) || (totalStars <= r.max);
            if (geMin && leMax) {
                Integer progress = (r.max == null) ? null : Math.max(0, r.max - (int) totalStars);
                return new LevelResDto(r.code, r.name, r.min, r.max, progress);
            }
        }

        return new LevelResDto("STARLIGHT_EXPLORER", "별빛 탐험가", 0, 9, Math.max(0, 9 - (int) totalStars));
    }
}
