package com.example.mydiary.data.diary;

import android.text.Spanned;

import com.example.mydiary.data.components.Mood;

import java.io.IOException;
import java.util.Date;

//Page düzenlemek için (bitirdikten sonra kapatmak gerekir)
public class OpenPage extends ModifiableDiaryPage {
    private final Diary diary;
    private final EditPage page;
    private final String pageId;

    OpenPage(Diary diary, String pageId, EditPage page) {
        super(pageId);
        this.diary = diary;
        this.pageId = pageId;
        this.page = page;
    }

    @Override
    public String getId(){
        return pageId;
    }

    @Override
    public String getTitle() {
        return page.getTitle();
    }

    @Override
    public Mood getMood() {
        return page.getMood();
    }

    @Override
    public Date getDate() {
        return page.getDate();
    }

    @Override
    public Spanned getContent() {
        return page.getContent();
    }

    @Override
    public void setTitle(String title) throws TitleAlreadyExistsException {
        page.setTitle(title);
    }

    @Override
    public void setMood(Mood mood) {
        page.setMood(mood);
    }

    @Override
    public void setDate(Date date) {
        page.setDate(date);
    }

    @Override
    public void setContent(Spanned content) {
        page.setContent(content);
    }

    public void close() throws IOException {
        diary.storePage(pageId, page);
    }
}
