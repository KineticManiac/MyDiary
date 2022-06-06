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
import java.util.UUID;

public class Diary implements Iterable<Diary.ViewPage> {
    public static final Page EMPTY_PAGE = new Page() {
        @Override
        public String getTitle() {
            return "My Diary Page";
        }

        @Override
        public Mood getMood() {
            return Mood.DEFAULT;
        }

        @Override
        public Spanned getContent() {
            return new SpannedString("");
        }
    };
    private final DiaryPageSet diaryPageSet;
    private final Context context;
    private final Registry registry;
    private final ContentRegisterer contentRegisterer;
    private final String registerName;
    private final DiaryPageSetRegisterer diaryPageSetRegisterer;

    public Diary(Context context, Registry registry, String registerName) throws IOException{
        this.context = context;
        this.registry = registry;
        this.contentRegisterer = new ContentRegisterer(context, registry);
        this.registerName = registerName;
        this.diaryPageSetRegisterer = new DiaryPageSetRegisterer();
        this.diaryPageSet = diaryPageSetRegisterer.loadOrCreate(registerName);
    }

    public ViewPage viewPageById(String pageId){
        return new ViewPage(diaryPageSet.getById(pageId));
    }

    public ViewPage viewPageByTitle(String title){
        return new ViewPage(diaryPageSet.getByTitle(title));
    }

    public EditPage editPage(Page page) throws IOException {
        return new EditPage(page);
    }

    public EditPage editPageById(String pageId) throws IOException {
        return new EditPage(diaryPageSet.getById(pageId));
    }

    public EditPage editPageByTitle(String title) throws IOException {
        return new EditPage(diaryPageSet.getByTitle(title));
    }

    public EditPage editEmptyPage(){
        try {
            return new EditPage(EMPTY_PAGE);
        }
        catch (IOException e){
            throw new RuntimeException(e); //Burada oluşmaması beklenir
        }
    }

    public void storePage(String pageId, Page page) throws IOException{
        diaryPageSet.put(pageId, page);
        diaryPageSetRegisterer.storeSet(registerName, diaryPageSet);
    }

    public String createPageId(){
        return UUID.randomUUID().toString();
    }

    public OpenPage createPage(){
        return new OpenPage(createPageId(), editEmptyPage());
    }

    public OpenPage openPage(ViewPage page) throws IOException{
        return new OpenPage(page.getId(), editPage(page));
    }

    public OpenPage openPageById(String pageId) throws IOException{
        return new OpenPage(pageId, editPageById(pageId));
    }

    public OpenPage openPageByTitle(String title) throws IOException{
        return openPage(viewPageByTitle(title));
    }


    @NonNull
    @Override
    public Iterator<ViewPage> iterator() {
        final Iterator<DiaryPage> iterator = diaryPageSet.iterator();
        return new Iterator<ViewPage>() {
            @Override
            public boolean hasNext() {
                return iterator().hasNext();
            }

            @Override
            public ViewPage next() {
                return new ViewPage(iterator.next());
            }
        };
    }

    public static class TitleAlreadyExistsException extends Exception{
        public TitleAlreadyExistsException(){
            super();
        }
        public TitleAlreadyExistsException(String description){
            super(description);
        }
    }

    private class DiaryPageSetRegisterer {
        DiaryPageSet loadOrCreate(String rName) throws IOException{
            DiaryPageSet set = loadSet(rName);
            return set != null ? set : new DiaryPageSet();
        }

        void storeSet(String rName, DiaryPageSet set) throws IOException {
            ObjectOutputStream oos = new ObjectOutputStream(registry.getOutputStream(rName));
            oos.writeObject(set.idMap);
            oos.close();
        }

        DiaryPageSet loadSet(String rName) throws IOException {
            InputStream is = registry.getInputStream(rName);
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
            return new DiaryPageSet(map);
        }
    }

    private class DiaryPageSet implements Iterable<DiaryPage>{
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
                destination = new DiaryPage(Diary.this, pageId, source);
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

        DiaryPageSet(){
            idMap = new HashMap<>();
            titleMap = new HashMap<>();
        }

        DiaryPageSet(HashMap<String, DiaryPage> idMap){
            this.idMap = idMap;
            titleMap = new HashMap<>();
            for(DiaryPage page : idMap.values()){
                page.diary = Diary.this;
                titleMap.put(page.title, page);
            }
        }

        @NonNull
        @Override
        public Iterator<DiaryPage> iterator() {
            return values().iterator();
        }
    }

    public interface Page {
        String getTitle();
        Mood getMood();
        Spanned getContent() throws IOException;
    }

    public interface ModifiablePage extends Page{
        void setTitle(String string) throws TitleAlreadyExistsException;
        void setMood(Mood mood);
        void setContent(Spanned content) throws IOException;
    }

    //Sadece Diary'de kullanmak için
    private static class DiaryPage implements Serializable, ModifiablePage {
        private transient Diary diary;
        private final String pageId;
        private String title;
        private Mood mood;

        private DiaryPage(Diary diary, String pageId, Page page) throws IOException{
            this.diary = diary;

            assert !diary.diaryPageSet.hasId(pageId);

            this.pageId = pageId;
            copyFrom(page);
        }

        private void copyFrom(Page page) throws IOException{
            String pageTitle = page.getTitle();
            if(title == null || !title.equals(pageTitle))
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

        String getId(){
            return pageId;
        }

        public Spanned getContent() throws IOException {
            return new ContentRegisterer(diary.context, diary.registry).load(pageId);
        }

        public void setTitle(String title) throws TitleAlreadyExistsException{
            if(diary.diaryPageSet.hasTitle(title))
                throw new TitleAlreadyExistsException("Title " + title + " already exists.");
            this.title = title;
        }

        public void setMood(Mood mood) {
            this.mood = mood;
        }

        public void setContent(Spanned content) throws IOException {
            diary.contentRegisterer.store(pageId, content);
        }
    }

    //Diary'deki DiaryPage'leri izlemek için
    public static class ViewPage implements Page{
        private final DiaryPage page;

        private ViewPage(DiaryPage page){
            this.page = page;
        }

        public String getId() { return page.getId(); }

        public String getTitle() {
            return page.getTitle();
        }

        public Mood getMood() {
            return page.getMood();
        }

        public Spanned getContent() throws IOException {
            return page.getContent();
        }
    }

    //Page düzenlemek için (Bitirdikten sonra tekrar geri yüklemek gerekir)
    public class EditPage implements ModifiablePage {
        private String title;
        private Mood mood;
        private Spanned content;

        private EditPage(Page page) throws IOException {
            this.title = page.getTitle();
            this.mood = page.getMood();
            this.content = page.getContent();
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

        public void setTitle(String title) throws TitleAlreadyExistsException{
            if(diaryPageSet.hasTitle(title))
                    throw new TitleAlreadyExistsException("Title " + title + " already exists.");
            this.title = title;
        }

        public void setMood(Mood mood) {
            this.mood = mood;
        }

        public void setContent(Spanned content) {
            this.content = content;
        }
    }

    //Page düzenlemek için (bitirdikten sonra kapatmak gerekir)
    public class OpenPage implements ModifiablePage{
        private final EditPage page;
        private final String pageId;

        private OpenPage(String pageId, EditPage page){
            this.pageId = pageId;
            this.page = page;
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
        public Spanned getContent(){
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
        public void setContent(Spanned content) {
            page.setContent(content);
        }

        public void close() throws IOException{
            storePage(pageId, page);
        }
    }
}
