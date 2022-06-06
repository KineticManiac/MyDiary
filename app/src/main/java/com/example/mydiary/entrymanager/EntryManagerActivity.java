package com.example.mydiary.entrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mydiary.R;
import com.example.mydiary.entryeditor.EntryEditorActivity;

public class EntryManagerActivity extends AppCompatActivity {

    String pageId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_manager);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(view -> {
            Intent intent = new Intent(this, EntryEditorActivity.class);
            intent.putExtra("id", pageId);
            startActivityForResult(intent, 1);
        });
    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent intent){
        super.onActivityResult(reqCode, resCode, intent);

        pageId = intent.getStringExtra("id");
        Log.d("pageId", pageId);
    }
}