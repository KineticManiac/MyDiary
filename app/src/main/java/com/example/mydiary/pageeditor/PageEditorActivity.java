package com.example.mydiary.pageeditor;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mydiary.R;
import com.example.mydiary.data.diary.Diary;
import com.example.mydiary.data.diary.DiaryPage;
import com.example.mydiary.data.diary.OpenPage;
import com.example.mydiary.dialog.input.InputDialog;
import com.example.mydiary.dialog.input.InputDialogBuilder;
import com.example.mydiary.register.FileRegistry;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.Date;

public class PageEditorActivity extends AppCompatActivity {

    final InputDialogBuilder inputStringDialogBuilder = new InputDialogBuilder(this, InputDialog.STRING);

    EditImageText eit;
    Diary diary;
    OpenPage page;
    View snackbarAttacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_editor);

        snackbarAttacher = findViewById(android.R.id.content);
        String pageId = getIntent().getStringExtra("id");
        try {
            diary = new Diary(this, new FileRegistry(this), "diary");
            diary.open();
            page = pageId != null ? diary.openPageById(pageId) : diary.createPage();
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

        try {
            page.close();
        }
        catch (IOException e)
        {
            Snackbar.make(snackbarAttacher, "Sayfa kaydedilemedi.", Snackbar.LENGTH_LONG).show();
            return;
        }
        diary.close();

        Intent result = new Intent();
        result.putExtra("id", page.getId());
        setResult(RESULT_OK, result);
        super.onBackPressed();
    }

    // create an action bar button
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        menu.clear();
        getMenuInflater().inflate(R.menu.page_editor_menu, menu);

        //Mood
        menu.findItem(R.id.action_mood).setTitle(page.getMood().toString());

        return true;
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
        else if(id == R.id.action_password){
            if(page.hasPassword()){
                tryChangePassword();
            }
            else{
                if(!setPassword(null))
                    throw new RuntimeException("Impossible State");
            }
            Snackbar.make(snackbarAttacher, "Password set.", Snackbar.LENGTH_SHORT).show();
            invalidateOptionsMenu();
        }
        else if(id == R.id.action_remove_password){
            if(page.hasPassword()){
                new InputDialogBuilder(this, InputDialog.PASSWORD)
                        .setTitle("Enter Password: ")
                        .setPositiveButton(data -> page.tryWithPassword((String) data, new DiaryPage.PasswordCallback() {
                            @Override
                            public void onSuccess(DiaryPage diaryPage) {
                                page.changePassword((String) data, null);
                                Snackbar.make(snackbarAttacher, "Password removed.", Snackbar.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFail(DiaryPage diaryPage) {
                                new AlertDialog.Builder(PageEditorActivity.this)
                                        .setPositiveButton("OK", ((dialogInterface, i) -> tryChangePassword()))
                                        .setMessage("Incorrect password.")
                                        .show();
                            }
                        }))
                        .setNegativeButton(null)
                        .create().show();
            }
            else{
                Snackbar.make(snackbarAttacher, "There is no password.", Snackbar.LENGTH_SHORT).show();
            }
        }
        else if(id == android.R.id.home) {
            onBackPressed();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private void tryChangePassword(){
        new InputDialogBuilder(this, InputDialog.PASSWORD)
                .setTitle("Enter Password: ")
                .setPositiveButton(data -> page.tryWithPassword((String) data, new DiaryPage.PasswordCallback() {
                    @Override
                    public void onSuccess(DiaryPage diaryPage) {
                        if(!setPassword((String) data))
                            throw new RuntimeException("Impossible state");
                    }

                    @Override
                    public void onFail(DiaryPage diaryPage) {
                        new AlertDialog.Builder(PageEditorActivity.this)
                                .setPositiveButton("OK", ((dialogInterface, i) -> tryChangePassword()))
                                .setMessage("Incorrect password.")
                                .show();
                    }
                }))
                .setNegativeButton(null)
                .create().show();
    }

    private boolean setPassword(String curPassword){
        if (page.hasPassword() && !page.tryPassword(curPassword))
            return false;

        new InputDialogBuilder(this, InputDialog.PASSWORD)
                .setTitle("Enter new password: ")
                .setPositiveButton(data1 ->
                    new InputDialogBuilder(this, InputDialog.PASSWORD)
                        .setPositiveButton(data2 -> {
                            if(data1.equals(data2) && !page.changePassword(curPassword, (String) data1))
                                throw new RuntimeException("Impossible state");
                        })
                        .setNegativeButton(null)
                        .setTitle("Re-ener password: ")
                        .create().show()
                )
                .setNegativeButton(null)
                .create().show();
        return true;
    }
}