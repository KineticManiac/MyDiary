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
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

//Sadece Diary'de kullanmak için
class InternalPage extends ModifiableDiaryPage implements Serializable {
    private transient Diary diary;
    private Date date;
    private String title;
    private Mood mood;

    InternalPage(Diary diary, String pageId, Page page) throws IOException {
        super(pageId);
        this.diary = diary;

        assert !diary.internalPageSet.hasId(pageId);

        copyFrom(page);
    }

    void copyFrom(Page page) throws IOException {
        String pageTitle = page.getTitle();
        if (title == null || !title.equals(pageTitle))
            title = pageTitle;

        date = page.getDate();
        mood = page.getMood();
        setContent(page.getContent());
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

    public String getId() {
        return pageId;
    }

    public Spanned getContent() throws IOException {
        return new ContentRegisterer(diary.context, diary.registry).load(pageId);
    }

    public void setTitle(String title) throws TitleAlreadyExistsException {
        if (diary.internalPageSet.hasTitle(title))
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

    public void setContent(Spanned content) throws IOException {
        diary.contentRegisterer.store(pageId, content);
    }

    @Override
    public void remove() throws IOException{
        diary.remove(this);
    }

    static class Set implements Iterable<InternalPage>{
        final Diary diary;
        final HashMap<String, InternalPage> idMap;
        final HashMap<String, InternalPage> titleMap;

        void put(String pageId, Page source) throws IOException{
            String title = source.getTitle();
            InternalPage destination = getById(pageId);
            if(destination != null){
                if(!title.equals(destination.title)){
                    titleMap.remove(destination.title);
                    titleMap.put(title, destination);
                }
                destination.copyFrom(source);
            }
            else{
                destination = new InternalPage(diary, pageId, source);
                idMap.put(pageId, destination);
                titleMap.put(title, destination);
            }
        }

        void remove(InternalPage page) throws IOException{
            assert idMap.containsValue(page);
            assert titleMap.containsValue(page);

            idMap.remove(page);
            titleMap.remove(page);
            diary.contentRegisterer.remove(page.pageId);
        }

        void remove(String pageId) throws IOException{
            InternalPage page = getById(pageId);
            if(page != null)
                remove(page);
        }

        InternalPage getById(String pageId){
            return idMap.get(pageId);
        }
        InternalPage getByTitle(String title) { return titleMap.get(title); }

        boolean has(InternalPage internalPage){
            return idMap.containsKey(internalPage.pageId);
        }
        boolean hasTitle(String title) { return titleMap.containsKey(title); }
        boolean hasId(String pageId) { return idMap.containsKey(pageId); }

        Collection<InternalPage> values(){
            return idMap.values();
        }

        Set(Diary diary){
            idMap = new HashMap<>();
            titleMap = new HashMap<>();
            this.diary = diary;
        }

        Set(Diary diary, HashMap<String, InternalPage> idMap){
            this.idMap = idMap;
            titleMap = new HashMap<>();
            this.diary = diary;
            for(InternalPage page : idMap.values()){
                page.diary = diary;
                titleMap.put(page.title, page);
            }
        }

        @NonNull
        @Override
        public Iterator<InternalPage> iterator() {
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
                HashMap<String, InternalPage> map;
                try {
                    map = (HashMap<String, InternalPage>) ois.readObject();
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
