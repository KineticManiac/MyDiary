package com.example.mydiary.data.diary;

import android.text.Spanned;

import com.example.mydiary.data.components.Mood;

import java.io.IOException;
import java.util.Date;

public interface ModifiablePage extends Page {
    void setTitle(String string) throws TitleAlreadyExistsException;
    void setMood(Mood mood);
    void setDate(Date date);
    void setContent(Spanned content) throws IOException;
}
