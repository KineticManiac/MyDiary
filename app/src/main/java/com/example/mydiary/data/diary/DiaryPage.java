package com.example.mydiary.data.diary;

import android.text.Spanned;

import androidx.annotation.NonNull;

import com.example.mydiary.data.other.ContentRegisterer;
import com.example.mydiary.data.components.Mood;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

//Sadece Diary'de kullanmak için
class DiaryPage implements Serializable, ModifiablePage {
    private transient Diary diary;
    private final String pageId;
    private String title;
    private Mood mood;

    DiaryPage(Diary diary, String pageId, Page page) throws IOException {
        this.diary = diary;

        assert !diary.diaryPageSet.hasId(pageId);

        this.pageId = pageId;
        copyFrom(page);
    }

    private void copyFrom(Page page) throws IOException {
        String pageTitle = page.getTitle();
        if (title == null || !title.equals(pageTitle))
            title = pageTitle;
        setMood(page.getMood());
        setContent(page.getContent());
    }

    public String getTitle() {
        return title;
    }

    public Mood getMood() {
        return mood;
    }

    String getId() {
        return pageId;
    }

    public Spanned getContent() throws IOException {
        return new ContentRegisterer(diary.context, diary.registry).load(pageId);
    }

    public void setTitle(String title) throws TitleAlreadyExistsException {
        if (diary.diaryPageSet.hasTitle(title))
            throw new TitleAlreadyExistsException("Title " + title + " already exists.");
        this.title = title;
    }

    public void setMood(Mood mood) {
        this.mood = mood;
    }

    public void setContent(Spanned content) throws IOException {
        diary.contentRegisterer.store(pageId, content);
    }

    static class Set implements Iterable<DiaryPage>{
        final Diary diary;
        final HashMap<String, DiaryPage> idMap;
        final HashMap<String, DiaryPage> titleMap;

        void put(String pageId, Page source) throws IOException{
            String title = source.getTitle();
            DiaryPage destination = getById(pageId);
            if(destination != null){
                if(!title.equals(destination.title)){
                    titleMap.remove(destination.title);
                    titleMap.put(title, destination);
                }
                destination.copyFrom(source);
            }
            else{
                destination = new DiaryPage(diary, pageId, source);
                idMap.put(pageId, destination);
                titleMap.put(title, destination);
            }
        }

        DiaryPage getById(String pageId){
            return idMap.get(pageId);
        }
        DiaryPage getByTitle(String title) { return titleMap.get(title); }

        boolean has(DiaryPage diaryPage){
            return idMap.containsKey(diaryPage.pageId);
        }
        boolean hasTitle(String title) { return titleMap.containsKey(title); }
        boolean hasId(String pageId) { return idMap.containsKey(pageId); }

        Collection<DiaryPage> values(){
            return idMap.values();
        }

        Set(Diary diary){
            idMap = new HashMap<>();
            titleMap = new HashMap<>();
            this.diary = diary;
        }

        Set(Diary diary, HashMap<String, DiaryPage> idMap){
            this.idMap = idMap;
            titleMap = new HashMap<>();
            this.diary = diary;
            for(DiaryPage page : idMap.values()){
                page.diary = diary;
                titleMap.put(page.title, page);
            }
        }

        @NonNull
        @Override
        public Iterator<DiaryPage> iterator() {
            return values().iterator();
        }
        static class Registerer {
            private final Diary diary;

            Registerer(Diary diary){
                this.diary = diary;
            }

            Set loadOrCreate(String rName) throws IOException{
                Set set = loadSet(rName);
                return set != null ? set : new Set(diary);
            }

            void storeSet(String rName, Set set) throws IOException {
                ObjectOutputStream oos = new ObjectOutputStream(diary.registry.getOutputStream(rName));
                oos.writeObject(set.idMap);
                oos.close();
            }

            Set loadSet(String rName) throws IOException {
                InputStream is = diary.registry.getInputStream(rName);
                if(is == null){
                    return null;
                }
                ObjectInputStream ois = new ObjectInputStream(is);
                HashMap<String, DiaryPage> map;
                try {
                    map = (HashMap<String, DiaryPage>) ois.readObject();
                }
                catch (ClassNotFoundException e){
                    throw new RuntimeException(e); //Bu hiçbir zaman olmamalı
                }
                ois.close();
                return new Set(diary, map);
            }
        }
    }
}
