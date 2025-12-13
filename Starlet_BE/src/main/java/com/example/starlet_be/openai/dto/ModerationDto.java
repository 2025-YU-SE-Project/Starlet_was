package com.example.starlet_be.openai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class ModerationDto {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ModerationRequest {
        private String input;
    }

    @Data
    public static class ModerationResponse {
        private String id;
        private String model;
        private List<Result> results;

        @Data
        public static class Result {
            private boolean flagged;
            private Categories categories;
            private CategoryScores category_scores;

            @Data
            public static class Categories {
                private boolean sexual;
                private boolean hate;
                private boolean harassment;
                private boolean self_harm;
                private boolean sexual_minors;
                private boolean hate_threatening;
                private boolean violence_graphic;
                private boolean self_harm_intent;
                private boolean self_harm_instructions;
                private boolean harassment_threatening;
                private boolean violence;
            }

            @Data
            public static class CategoryScores {
                private double sexual;
                private double hate;
                private double harassment;
                private double self_harm;
                private double sexual_minors;
                private double hate_threatening;
                private double violence_graphic;
                private double self_harm_intent;
                private double self_harm_instructions;
                private double harassment_threatening;
                private double violence;
            }
        }
    }
}
