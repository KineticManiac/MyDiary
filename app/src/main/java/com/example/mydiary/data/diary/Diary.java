package com.example.mydiary.data.diary;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.mydiary.data.other.ContentRegisterer;
import com.example.mydiary.register.Registry;

import java.io.IOException;
import java.util.Iterator;
import java.util.UUID;

public class Diary implements Iterable<ViewPage> {
    public static final String DEFAULT_REGISTER_NAME = "diary";

    final Registry registry;
    final ContentRegisterer contentRegisterer;
    final Context context;
    final DiaryPage.Set diaryPageSet;

    private final String registerName;
    private final DiaryPage.Set.Registerer diaryPageSetRegisterer;

    public Diary(Context context, Registry registry) throws IOException{
        this(context, registry, DEFAULT_REGISTER_NAME);
    }

    public Diary(Context context, Registry registry, String registerName) throws IOException{
        this.context = context;
        this.registry = registry;
        this.contentRegisterer = new ContentRegisterer(context, registry);
        this.registerName = registerName;
        this.diaryPageSetRegisterer = new DiaryPage.Set.Registerer(this);
        this.diaryPageSet = diaryPageSetRegisterer.loadOrCreate(registerName);

    }

    public ViewPage viewPageById(String pageId){
        return new ViewPage(diaryPageSet.getById(pageId));
    }

    public ViewPage viewPageByTitle(String title){
        return new ViewPage(diaryPageSet.getByTitle(title));
    }

    public EditPage editPage(Page page) throws IOException {
        return new EditPage(this, page);
    }

    public EditPage editPageById(String pageId) throws IOException {
        return new EditPage(this, diaryPageSet.getById(pageId));
    }

    public EditPage editPageByTitle(String title) throws IOException {
        return new EditPage(this, diaryPageSet.getByTitle(title));
    }

    public EditPage editEmptyPage(){
        try {
            return new EditPage(this, new EmptyPage(this));
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
        return new OpenPage(this, createPageId(), editEmptyPage());
    }

    public OpenPage openPage(ViewPage page) throws IOException{
        return new OpenPage(this, page.getId(), editPage(page));
    }

    public OpenPage openPageById(String pageId) throws IOException{
        return new OpenPage(this, pageId, editPageById(pageId));
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
                return iterator.hasNext();
            }

            @Override
            public ViewPage next() {
                return new ViewPage(iterator.next());
            }
        };
    }

}
