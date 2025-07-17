package com.example.se_be.entity.enums;

public enum Emotion {
    HAPPINESS,
    ANXIETY,
    NEUTRAL,
    CALM,
    ANGER,
    DEPRESSION;

    // 색상을 바로 가져올 수 있게
    public Color getColor() {
        return switch (this) {
            case HAPPINESS -> Color.YELLOW;
            case ANXIETY -> Color.ORANGE;
            case NEUTRAL -> Color.WHITE;
            case CALM -> Color.SKYBLUE;
            case ANGER -> Color.RED;
            case DEPRESSION -> Color.BLUE;
        };
    }
}
