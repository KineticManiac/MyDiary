package com.example.mydiary.pagemanager;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mydiary.R;
import com.example.mydiary.data.components.Mood;
import com.example.mydiary.data.diary.DiaryPage;
import com.example.mydiary.data.diary.Diary;
import com.example.mydiary.data.diary.OpenPage;
import com.example.mydiary.pageeditor.PageEditorActivity;
import com.example.mydiary.register.Registry;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.Date;

public class PageManagerActivity extends AppCompatActivity {
    private static final int EDITOR_OPEN_REQUEST = 1;

    private CoordinatorLayout coordinatorLayout;
    private RecyclerView recyclerView;
    private DiaryRecyclerViewAdapter recyclerViewAdapter;
    private Diary diary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_manager);

        coordinatorLayout = findViewById(com.google.android.material.R.id.layout);

        recyclerView = findViewById(R.id.manager_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        diary = new Diary(this, Registry.DEFAULT);
        openDiary();

        try{
            initialize();
        }
        catch(IOException e){
            throw new RuntimeException(e);
        }

        refresh();
    }

    void openDiary(){
        openDiary(true);
    }

    void openDiary(boolean must){
        try {
            diary.open();
        }
        catch (IOException e){
            Snackbar.make(coordinatorLayout,"Error: Can't load diary.", Snackbar.LENGTH_LONG).show();
            e.printStackTrace();
            if(must)
                finish();
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

    void refresh(){
        recyclerViewAdapter = new DiaryRecyclerViewAdapter(diary);
        recyclerViewAdapter.setOnClickListener(this::startEditingPage);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    private void startEditingPage(DiaryPage page){
        Intent intent = new Intent(this, PageEditorActivity.class);
        intent.putExtra("id", page.getId());
        diary.close();
        startActivityForResult(intent, EDITOR_OPEN_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);

        if(resultCode == RESULT_OK) {
            if (requestCode == EDITOR_OPEN_REQUEST){
                String id = intent.getStringExtra("id");
                openDiary();
                recyclerViewAdapter.reload(diary.viewPageById(id));
            }
        }
    }
}