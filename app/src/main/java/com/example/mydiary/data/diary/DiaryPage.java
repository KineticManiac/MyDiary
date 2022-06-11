package com.example.mydiary.data.diary;

import java.io.Serializable;

public abstract class DiaryPage implements Page, Serializable {
    protected final String pageId;
    protected DiaryPage(String pageId){
        this.pageId = pageId;
    }
    public String getId(){
        return pageId;
    }
    public int hashCode(){
        return getId().hashCode();
    }
    public boolean equals(Object o){
        if(o instanceof DiaryPage){
            return getId().equals(((DiaryPage) o).getId());
        }
        else{
            return false;
        }
    }
}
