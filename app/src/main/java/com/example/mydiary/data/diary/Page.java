package com.example.mydiary.data.diary;

import android.text.Spanned;

import com.example.mydiary.data.components.Mood;

import java.io.IOException;
import java.util.Date;

public interface Page extends Comparable<Page> {
    String getTitle();
    Mood getMood();
    Date getDate();
    Spanned getContent() throws IOException;

    default int compareTo(Page p){
        return Page.compare(this, p);
    }
    static int compare(Page p1, Page p2){
        return -p1.getDate().compareTo(p2.getDate());
    }
}
