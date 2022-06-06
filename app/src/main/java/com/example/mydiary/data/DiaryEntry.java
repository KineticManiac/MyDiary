package com.example.mydiary.data;

import android.location.Location;
import android.text.Spanned;
import android.text.SpannedString;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Date;

public class DiaryEntry {
    static public Spanned EMPTY_CONTENT = new SpannedString("");

    private final Manager manager;
    private @NonNull Mood mood;
    private @NonNull String title;
    private @Nullable Location location; //sadece coğrafi bölge için, timestamp'i boşver
    private @Nullable Date date;
    private @Nullable Spanned content;

    private DiaryEntry(Manager manager, @NonNull String title, @NonNull Mood mood, @Nullable Location location, @Nullable Date date){
        this.manager = manager;
        this.title = title;
        this.mood = mood;
        this.location = location;
        this.date = date;
        this.content = null;
    }

    @NonNull
    public Mood getMood() {
        return mood;
    }

    public void setMood(@NonNull Mood mood) {
        this.mood = mood;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    @Nullable
    public Date getDate() {
        return date;
    }

    public void setDate(@Nullable Date date) {
        this.date = date;
    }

    @Nullable
    public Location getLocation() {
        return location;
    }

    public void setLocation(@Nullable Location location) {
        this.location = location;
    }

    @NonNull
    public Spanned getContent() {
        if(content == null)
            return EMPTY_CONTENT;
        else
            return content;
    }

    public void setContent(@Nullable Spanned content) {
        this.content = content;
    }

    public static class Manager {

    }
}
