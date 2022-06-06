package com.example.mydiary.data;

import android.content.Context;
import android.text.Spanned;
import android.text.SpannedString;

import androidx.annotation.NonNull;

import com.example.mydiary.register.Registry;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

public class Diary{
    public static Spanned EMPTY_CONTENT = new SpannedString("");
    private final DiaryPageSet diaryPageSet;
    private final Registry registry;
    private final String registerName;
    private final DiaryPageSetFactory diaryPageSetFactory;

    public Diary(Registry registry, String registerName) throws IOException{
        this.registry = registry;
        this.registerName = registerName;
        this.diaryPageSetFactory = new DiaryPageSetFactory();
        this.diaryPageSet = diaryPageSetFactory.loadOrCreate(registerName);
    }

    public static class TitleAlreadyExistsException extends Exception{ }

    private class DiaryPageSetFactory {
        DiaryPageSet loadOrCreate(String rName) throws IOException{
            DiaryPageSet set = storeSet(rName);
            return set != null ? set : new DiaryPageSet();
        }

        void loadSet(String rName, DiaryPageSet set) throws IOException {
            ObjectOutputStream oos = new ObjectOutputStream(registry.getOutputStream(rName));
            oos.writeObject(set);
            oos.close();
        }

        DiaryPageSet storeSet(String rName) throws IOException {
            InputStream is = registry.getInputStream(rName);
            if(is == null){
                return null;
            }
            ObjectInputStream ois = new ObjectInputStream(is);
            DiaryPageSet map;
            try {
                map = (DiaryPageSet) ois.readObject();
            }
            catch (ClassNotFoundException e){
                throw new RuntimeException(e); //Bu hiçbir zaman olmamalı
            }
            ois.close();
            return map;
        }
    }

    private class DiaryPageSet implements Serializable, Iterable<DiaryPage>{
        final HashMap<String, DiaryPage> idMap;
        final HashMap<String, DiaryPage> titleMap;

        void put(DiaryPage diaryPage) throws TitleAlreadyExistsException{
            if(!diaryPage.pageId.equals(getByTitle(diaryPage.title).pageId))
                throw new TitleAlreadyExistsException();
            titleMap.put(diaryPage.title, diaryPage);
            idMap.put(diaryPage.pageId, diaryPage);
        }

        DiaryPage getById(String pageId){
            return idMap.get(pageId);
        }
        DiaryPage getByTitle(String title) { return titleMap.get(title); }

        boolean has(DiaryPage diaryPage){
            return idMap.containsKey(diaryPage.pageId);
        }
        boolean hasTitle(String title) { return titleMap.containsKey(title); }

        Collection<DiaryPage> values(){
            return idMap.values();
        }

        DiaryPageSet(){
            idMap = new HashMap<>();
            titleMap = new HashMap<>();
        }

        @NonNull
        @Override
        public Iterator<DiaryPage> iterator() {
            return values().iterator();
        }
    }
    //Sadece Diary'de kullanmak için
    private class DiaryPage implements Serializable {
        private final String pageId;
        private String title;
        private Mood mood;

        private DiaryPage(String pageId){
            this.pageId = pageId;
        }

        public String getTitle() {
            return title;
        }

        public Mood getMood() {
            return mood;
        }

        public Spanned getContent(Context context, Registry registry) throws IOException {
            return new ContentRegisterer(context, registry).load(pageId);
        }

        public boolean setTitle(String title) {
            for(DiaryPage page : diaryPageSet.values()){
                if(page.title.equals(title))
                    return false;
            }
            this.title = title;
            return true;
        }

        public void setMood(Mood mood) {
            this.mood = mood;
        }

        public void setContent(Context context, Registry registry, Spanned content) throws IOException {
            new ContentRegisterer(context, registry).store(pageId, content);
        }
    }

    //Diary'deki DiaryPage'leri izlemek için

    public class ViewPage{
        private final DiaryPage page;

        private ViewPage(DiaryPage page){
            this.page = page;
        }

        public String getTitle() {
            return page.getTitle();
        }

        public Mood getMood() {
            return page.getMood();
        }

        public Spanned getContent(Context context, Registry registry) throws IOException {
            return page.getContent(context, registry);
        }
    }

    //Page editlemek için (Bitirdikten sonra tekrar geri yüklemek gerekir)
    public class EditPage{
        private final String pageId;
        private String title;
        private Mood mood;
        private Spanned content;

        private EditPage(Context context, Registry registry, DiaryPage page) throws IOException {
            this.pageId = page.pageId;
            this.title = page.title;
            this.mood = page.mood;
            this.content = page.getContent(context, registry);
        }

        private String getPageId(){
            return pageId;
        }

        public String getTitle() {
            return title;
        }

        public Mood getMood() {
            return mood;
        }

        public Spanned getContent(){
            return content;
        }

        public boolean setTitle(String title) {
            for(DiaryPage page : diaryPageSet.values()){
                if(page.title.equals(title))
                    return false;
            }
            this.title = title;
            return true;
        }

        public void setMood(Mood mood) {
            this.mood = mood;
        }

        public void setContent(Spanned content) {
            this.content = content;
        }
    }
}
