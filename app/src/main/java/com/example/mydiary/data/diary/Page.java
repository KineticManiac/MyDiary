package com.example.mydiary.data.diary;

import android.text.Spanned;

import com.example.mydiary.data.components.Mood;

import java.io.IOException;

public interface Page {
    String getTitle();

    Mood getMood();

    Spanned getContent() throws IOException;
}
