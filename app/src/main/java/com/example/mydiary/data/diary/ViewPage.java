package com.example.mydiary.data.diary;

import android.text.Spanned;

import com.example.mydiary.data.components.Mood;

import java.io.IOException;
import java.util.Date;

//Diary'deki DiaryPage'leri izlemek için
public class ViewPage implements Page {
    private final DiaryPage page;

    ViewPage(DiaryPage page) {
        this.page = page;
    }

    public String getId() {
        return page.getId();
    }

    public String getTitle() {
        return page.getTitle();
    }

    public Mood getMood() {
        return page.getMood();
    }

    @Override
    public Date getDate() {
        return page.getDate();
    }

    public Spanned getContent() throws IOException {
        return page.getContent();
    }
}
