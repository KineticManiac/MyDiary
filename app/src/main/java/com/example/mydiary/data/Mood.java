package com.example.mydiary.data;

import androidx.annotation.NonNull;

import java.io.Serializable;

public enum Mood implements Serializable {
    NEUTRAL("\uD83D\uDE10"),
    HAPPY("\uD83D\uDE42"),
    ECSTATIC("\uD83D\uDE01"),
    SAD("\uD83D\uDE41"),
    DEPRESSED("\uD83D\uDE2D"),
    ANGRY("\uD83D\uDE20"),
    ENRAGED("\uD83D\uDE21");

    static public Mood DEFAULT = NEUTRAL;

    private final String emoji;
    Mood(String emoji){
        this.emoji = emoji;
    }

    public Mood getNext(){
        switch (this){
            case NEUTRAL:
                return HAPPY;
            case HAPPY:
                return ECSTATIC;
            case ECSTATIC:
                return SAD;
            case SAD:
                return DEPRESSED;
            case DEPRESSED:
                return ANGRY;
            case ANGRY:
                return ENRAGED;
            case ENRAGED:
                return NEUTRAL;
            default:
                throw new RuntimeException("Unreachable code!");
        }
    }

    @NonNull
    @Override
    public String toString() {
        return emoji;
    }
}
