package com.example.mydiary.entryeditor;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mydiary.R;
import com.example.mydiary.data.Diary;
import com.example.mydiary.dialog.InputDialog;
import com.example.mydiary.dialog.InputDialogBuilder;
import com.example.mydiary.register.Registry;

public class EntryEditorActivity extends AppCompatActivity {

    final InputDialogBuilder inputStringDialogBuilder = new InputDialogBuilder(this, InputDialog.STRING);

    EditImageText eit;
    String pageId;
    Diary diary;
    Diary.EditPage page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_editor);
        pageId = getIntent().getStringExtra("id");
        try {
            diary = new Diary(this, Registry.DEFAULT, "diary");
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
        if(pageId == null){
            pageId = diary.createPageId();
        }
        try {
            diary.storePage(pageId, page);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        Intent result = new Intent();
        result.putExtra("id", pageId);
        setResult(RESULT_OK, result);
        super.onBackPressed();
    }

    // create an action bar button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.diary_entry_menu, menu);

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