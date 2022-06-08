package com.example.mydiary.data.diary;

import android.text.Spanned;

import com.example.mydiary.data.components.Mood;

import java.io.IOException;

//Page düzenlemek için (Bitirdikten sonra tekrar geri yüklemek gerekir)
public class EditPage implements ModifiablePage {
    private final Diary diary;
    private String title;
    private Mood mood;
    private Spanned content;

    EditPage(Diary diary, Page page) throws IOException {
        this.diary = diary;
        this.title = page.getTitle();
        this.mood = page.getMood();
        this.content = page.getContent();
    }

    public String getTitle() {
        return title;
    }

    public Mood getMood() {
        return mood;
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

    public void setContent(Spanned content) {
        this.content = content;
    }
}
