package com.example.mydiary.pageeditor;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mydiary.R;
import com.example.mydiary.data.diary.Diary;
import com.example.mydiary.data.diary.EditPage;
import com.example.mydiary.dialog.input.InputDialog;
import com.example.mydiary.dialog.input.InputDialogBuilder;
import com.example.mydiary.register.FileRegistry;
import com.example.mydiary.register.Registry;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.Date;

public class PageEditorActivity extends AppCompatActivity {

    final InputDialogBuilder inputStringDialogBuilder = new InputDialogBuilder(this, InputDialog.STRING);

    EditImageText eit;
    String pageId;
    Diary diary;
    EditPage page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_editor);
        pageId = getIntent().getStringExtra("id");
        try {
            diary = new Diary(this, new FileRegistry(this), "diary");
            diary.open();
            page = pageId != null ? diary.editPageById(pageId) : diary.editEmptyPage();
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }

        setTitle(page.getTitle());

        eit = findViewById(R.id.editImageText);
        eit.setText(page.getContent());
    }

    @Override
    public void onBackPressed(){
        page.setContent(eit.getText());
        page.setDate(new Date());

        if(pageId == null){
            pageId = Diary.createPageId();
        }

        try {
            diary.storePage(pageId, page);
        }
        catch (IOException e)
        {
            Snackbar.make(findViewById(android.R.id.content), "Sayfa kaydedilemedi.", Snackbar.LENGTH_LONG).show();
            return;
        }
        diary.close();

        Intent result = new Intent();
        result.putExtra("id", pageId);
        setResult(RESULT_OK, result);
        super.onBackPressed();
    }

    // create an action bar button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.page_editor_menu, menu);

        //Mood
        menu.findItem(R.id.action_mood).setTitle(page.getMood().toString());

        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //switch kullanınca warning aldım
        if(id == R.id.action_mood) {
            page.setMood(page.getMood().getNext());
            item.setTitle(page.getMood().toString());
        }
        else if(id == R.id.action_title){
            inputStringDialogBuilder.reset()
                    .setTitle("Enter Title")
                    .setPositiveButton(object -> {
                        String string = (String) object;
                        try {
                            page.setTitle(string);
                        }
                        catch (Exception e){
                            throw new RuntimeException(e);
                        }
                        setTitle(string);
                    })
                    .setNegativeButton(null)
                    .create().show();
        }
        else if(id == android.R.id.home) {
            onBackPressed();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }
}