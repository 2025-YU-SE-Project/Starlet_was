package com.example.starlet_be.domains.diary.entity;

public enum Emotion {
    HAPPINESS,
    FUNNY,
    NEUTRAL,
    SURPRISING,
    ANGER,
    SADNESS;

    // 색상을 바로 가져올 수 있게
    public Color getColor() {
        return switch (this) {
            case HAPPINESS -> Color.YELLOW;
            case FUNNY -> Color.ORANGE;
            case NEUTRAL -> Color.GREEN;
            case SURPRISING -> Color.PURPLE;
            case ANGER -> Color.RED;
            case SADNESS -> Color.BLUE;
        };
    }
}
