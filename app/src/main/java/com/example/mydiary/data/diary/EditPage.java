package com.example.mydiary.data.diary;

import android.text.Spanned;

import com.example.mydiary.data.components.Mood;

import java.io.IOException;
import java.util.Date;

//Page düzenlemek için (Bitirdikten sonra tekrar geri yüklemek gerekir)
public class EditPage implements ModifiablePage {
    private final Diary diary;
    private String title;
    private Mood mood;
    private Spanned content;
    private Date date;

    EditPage(Diary diary, Page page) throws IOException {
        this.diary = diary;
        this.title = page.getTitle();
        this.mood = page.getMood();
        this.content = page.getContent();
        this.date = page.getDate();
    }

    public String getTitle() {
        return title;
    }

    public Mood getMood() {
        return mood;
    }

    @Override
    public Date getDate() {
        return date;
    }

    public Spanned getContent() {
        return content;
    }

    public void setTitle(String title) throws TitleAlreadyExistsException {
        if (diary.diaryPageSet.hasTitle(title))
            throw new TitleAlreadyExistsException("Title " + title + " already exists.");
        this.title = title;
    }

    public void setMood(Mood mood) {
        this.mood = mood;
    }

    @Override
    public void setDate(Date date) {
        this.date = date;
    }

    public void setContent(Spanned content) {
        this.content = content;
    }
}
