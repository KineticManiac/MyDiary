package com.example.mydiary.pagemanager;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mydiary.R;
import com.example.mydiary.data.components.Mood;
import com.example.mydiary.data.diary.Diary;
import com.example.mydiary.data.diary.OpenPage;
import com.example.mydiary.register.Registry;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class PageManagerActivity extends AppCompatActivity {

    CoordinatorLayout coordinatorLayout;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_manager);

        coordinatorLayout = findViewById(com.google.android.material.R.id.layout);

        recyclerView = findViewById(R.id.manager_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Diary diary;
        try {
            diary = new Diary(this, Registry.DEFAULT);

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
        catch (IOException e){
            Snackbar.make(coordinatorLayout,"Error: Can't load diary.", Snackbar.LENGTH_LONG).show();
            return;
        }

        DiaryRecyclerViewAdapter adapter = new DiaryRecyclerViewAdapter(diary);
        recyclerView.setAdapter(adapter);
    }
}