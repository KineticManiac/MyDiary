package com.example.mydiary.data.diary;

import java.io.Serializable;

public abstract class DiaryPage implements Page, Serializable {
    protected final String pageId;
    protected DiaryPage(String pageId){
        this.pageId = pageId;
    }

    abstract String getPassword();
    abstract void setPassword(String password);
    public boolean hasPassword(){
        return getPassword() != null;
    }
    public boolean tryPassword(String attempt){
        String password = getPassword();
        return password != null ? password.equals(attempt) : true;
    }
    public void tryWithPassword(String attempt, PasswordCallback passwordCallback){
        if(tryPassword(attempt))
            passwordCallback.onSuccess(this);
        else
            passwordCallback.onFail(this);
    }
    public boolean changePassword(String curPassword, String newPassword){
        boolean result = tryPassword(curPassword);
        if(result)
            setPassword(newPassword);
        return result;
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
    public interface PasswordCallback{
        void onSuccess(DiaryPage diaryPage);
        void onFail(DiaryPage diaryPage);
    }
}
