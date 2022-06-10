package com.example.mydiary.data.diary;

import android.text.Spanned;

import com.example.mydiary.data.components.Mood;

import java.io.IOException;
import java.util.Date;

public interface Page {
    String getTitle();
    Mood getMood();
    Date getDate();
    Spanned getContent() throws IOException;
}
