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

    InternalPage.Set internalPageSet;

    private final String registerName;
    private final InternalPage.Set.Registerer internalPageSetRegisterer;

    private boolean active;

    public Diary(Context context, Registry registry){
        this(context, registry, DEFAULT_REGISTER_NAME);
    }

    public Diary(Context context, Registry registry, String registerName){
        this.context = context;
        this.registry = registry;
        this.contentRegisterer = new ContentRegisterer(context, registry);
        this.registerName = registerName;
        this.internalPageSetRegisterer = new InternalPage.Set.Registerer(this);
        active = false;
    }

    public void open() throws IOException {
        active = true;
        internalPageSet = internalPageSetRegisterer.loadOrCreate(registerName);
    }

    public void close() {
        active = false;
    }

    public ViewPage viewPageById(String pageId){
        assert active;
        return new ViewPage(internalPageSet.getById(pageId));
    }

    public ViewPage viewPageByTitle(String title){
        assert active;
        return new ViewPage(internalPageSet.getByTitle(title));
    }

    public EditPage editPage(Page page) throws IOException {
        assert active;
        return new EditPage(this, page);
    }

    public EditPage editPageById(String pageId) throws IOException {
        assert active;
        return new EditPage(this, internalPageSet.getById(pageId));
    }

    public EditPage editPageByTitle(String title) throws IOException {
        assert active;
        return new EditPage(this, internalPageSet.getByTitle(title));
    }

    public EditPage editEmptyPage(){
        assert active;
        try {
            return new EditPage(this, new EmptyPage(this));
        }
        catch (IOException e){
            throw new RuntimeException(e); //Burada oluşmaması beklenir
        }
    }

    public void storePage(String pageId, Page page) throws IOException{
        assert active;
        internalPageSet.put(pageId, page);
        internalPageSetRegisterer.storeSet(registerName, internalPageSet);
    }

    public static String createPageId(){
        return UUID.randomUUID().toString();
    }

    public OpenPage createPage(){
        assert active;
        return new OpenPage(this, createPageId(), editEmptyPage());
    }

    public OpenPage openPage(ViewPage page) throws IOException{
        assert active;
        return new OpenPage(this, page.getId(), editPage(page));
    }

    public OpenPage openPageById(String pageId) throws IOException{
        assert active;
        return new OpenPage(this, pageId, editPageById(pageId));
    }

    public OpenPage openPageByTitle(String title) throws IOException{
        assert active;
        return openPage(viewPageByTitle(title));
    }

    @NonNull
    @Override
    public Iterator<ViewPage> iterator() {
        assert active;
        final Iterator<InternalPage> iterator = internalPageSet.iterator();
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
