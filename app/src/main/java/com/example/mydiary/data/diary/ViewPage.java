package com.example.mydiary.data.diary;

import android.text.Spanned;

import com.example.mydiary.data.components.Mood;

import java.io.IOException;
import java.util.Date;

//Diary'deki DiaryPage'leri izlemek için
public class ViewPage extends DiaryPage {
    private final InternalPage page;

    ViewPage(InternalPage page) {
        super(page.pageId);
        this.page = page;
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

    @Override
    String getPassword() {
        return page.getPassword();
    }

    @Override
    void setPassword(String password) {
        page.setPassword(password);
    }
}
