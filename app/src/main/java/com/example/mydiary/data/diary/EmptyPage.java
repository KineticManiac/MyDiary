package com.example.mydiary.data.diary;

import android.text.Spanned;
import android.text.SpannedString;

import com.example.mydiary.data.components.Mood;

import java.util.Date;

//Immutable empty page. Bundan EditPage oluşturulabilir.
class EmptyPage implements Page {
    static final String DEFAULT_EMPTY_PAGE_TITLE = "My Diary Page";
    static final SpannedString EMPTY_SPANNED_STRING = new SpannedString("");

    private final Diary diary;
    private final String title;

    EmptyPage(Diary diary) {
        this.diary = diary;
        int i = 1;
        String title = generateTitle(i);
        while (diary.internalPageSet.hasTitle(title)) {
            i++;
            title = generateTitle(i);
        }
        this.title = title;
    }

    private String generateTitle(int i) {
        if (i == 1) {
            return DEFAULT_EMPTY_PAGE_TITLE;
        } else {
            return DEFAULT_EMPTY_PAGE_TITLE + " " + i;
        }
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public Mood getMood() {
        return Mood.DEFAULT;
    }

    @Override
    public Date getDate() {
        return new Date();
    }

    @Override
    public Spanned getContent() {
        return EMPTY_SPANNED_STRING;
    }
}
