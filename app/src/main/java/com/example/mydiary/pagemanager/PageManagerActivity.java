package com.example.mydiary.pagemanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ContentFrameLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mydiary.R;
import com.example.mydiary.data.components.Mood;
import com.example.mydiary.data.diary.Diary;
import com.example.mydiary.data.diary.DiaryPage;
import com.example.mydiary.data.diary.OpenPage;
import com.example.mydiary.pageeditor.PageEditorActivity;
import com.example.mydiary.register.FileRegistry;
import com.example.mydiary.register.Registry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class PageManagerActivity extends AppCompatActivity {
    private static final int EDITOR_OPEN_REQUEST = 1;
    private static final int EDITOR_CREATE_REQUEST = 2;

    private ContentFrameLayout snackbarAttacher;
    private DiaryRecyclerViewAdapter recyclerViewAdapter;
    private Diary diary;

    private final ArrayList<DiaryPage> removeList = new ArrayList<>();
    private boolean removing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_manager);

        snackbarAttacher = findViewById(android.R.id.content);

        FloatingActionButton addButton = findViewById(R.id.manager_add_button);
        addButton.setOnClickListener(view -> createNewPage());

        RecyclerView recyclerView = findViewById(R.id.manager_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        diary = new Diary(this, new FileRegistry(this));
        if (!openDiary())
            return;

        /*
        try{
            initialize();
        }
        catch(IOException e){
            throw new RuntimeException(e);
        }
         */

        recyclerViewAdapter = new DiaryRecyclerViewAdapter(diary);
        setRemoving(false);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    private void createNewPage() {
        Intent intent = new Intent(this, PageEditorActivity.class);
        intent.putExtra("id", (String) null);
        diary.close();
        startActivityForResult(intent, EDITOR_CREATE_REQUEST);
    }

    boolean openDiary(){
        try {
            diary.open();
            return true;
        }
        catch (IOException e){
            Snackbar.make(snackbarAttacher,"Error: Can't load diary.", Snackbar.LENGTH_INDEFINITE).show();
            e.printStackTrace();
            return false;
        }
    }

    void initialize() throws IOException{
        Mood mood = Mood.DEFAULT;
        Date date = new Date();
        for(int i = 0; i < 100; i++) {
            OpenPage page = diary.createPage();

            page.setMood(mood);
            mood = mood.getNext();

            page.setDate(date);
            date = new Date(date.getTime() + 24 * 60 * 60 * 1000);

            page.close();
        }
    }

    private void setRemoving(boolean removing){
        this.removing = removing;
        if(removing){
            removeList.clear();
            recyclerViewAdapter.setOnClickListener(this::onAddPageToRemoveList);
            recyclerViewAdapter.setOnLongClickListener(null);
        }
        else{
            for(DiaryPage page : removeList){
                recyclerViewAdapter.setDiaryPageSelected(page, false);
            }
            recyclerViewAdapter.setOnClickListener(this::onEditPage);
            recyclerViewAdapter.setOnLongClickListener(this::onStartRemoving);
        }
        invalidateOptionsMenu();
    }

    private boolean onStartRemoving(DiaryPage page){
        setRemoving(true);
        recyclerViewAdapter.setDiaryPageSelected(page, true);
        removeList.add(page);
        return true;
    }

    private void onAddPageToRemoveList(DiaryPage page){
        if(removeList.contains(page)){
            recyclerViewAdapter.setDiaryPageSelected(page, false);
            removeList.remove(page);
        }
        else{
            recyclerViewAdapter.setDiaryPageSelected(page, true);
            removeList.add(page);
        }
    }

    private void onEditPage(DiaryPage page){
        Intent intent = new Intent(this, PageEditorActivity.class);
        intent.putExtra("id", page.getId());
        diary.close();
        startActivityForResult(intent, EDITOR_OPEN_REQUEST);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        super.onPrepareOptionsMenu(menu);

        if(removing) {
            getMenuInflater().inflate(R.menu.page_manager_menu, menu);
            menu.findItem(R.id.manager_menu_cancel).setOnMenuItemClickListener(item -> {
                setRemoving(false);
                return true;
            });
            menu.findItem(R.id.manager_menu_remove).setOnMenuItemClickListener(item -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Are you sure you want to delete " + removeList.size() + " messages?")
                        .setPositiveButton("Yes", (dialogInterface, i) -> {
                            int totalCounter = 0;
                            int exCounter = 0;
                            for(DiaryPage page : removeList){
                                totalCounter++;

                                try {
                                    diary.remove(page);
                                    recyclerViewAdapter.remove(page);
                                }
                                catch (IOException e){
                                    exCounter++;
                                    e.printStackTrace();
                                }
                            }

                            String message;
                            if(exCounter == 0){
                                message = totalCounter + " messages were successfully removed.";
                            }
                            else{
                                message = (totalCounter - exCounter) + " messages were successfully removed. "
                                        + exCounter + " messages were not removed due to an error.";
                            }
                            Snackbar.make(snackbarAttacher, message, Snackbar.LENGTH_SHORT).show();

                            setRemoving(false);
                        })
                .setNegativeButton("Cancel", ((dialogInterface, i) -> {}))
                .show();

                return true;
            });
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);

        if(resultCode == RESULT_OK) {
            String id;
            switch (requestCode){
                case EDITOR_OPEN_REQUEST:
                    id = intent.getStringExtra("id");
                    if(!openDiary())
                        return;
                    recyclerViewAdapter.reload(diary.viewPageById(id));
                    break;
                case EDITOR_CREATE_REQUEST:
                    id = intent.getStringExtra("id");
                    if(!openDiary())
                        return;
                    recyclerViewAdapter.add(diary.viewPageById(id));
                    break;
            }
        }
    }
}