package com.example.mydiary.data.diary;

import java.io.IOException;
import java.io.Serializable;

public abstract class ModifiableDiaryPage extends DiaryPage implements ModifiablePage, Serializable {
    protected ModifiableDiaryPage(String pageId) {
        super(pageId);
    }
    public abstract void remove() throws IOException;
}

