package com.example.mydiary.entryeditor;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mydiary.R;
import com.example.mydiary.dialog.InputDialog;
import com.example.mydiary.dialog.InputDialogBuilder;
import com.example.mydiary.data.DiaryEntry;

public class EntryEditorActivity extends AppCompatActivity {

    final InputDialogBuilder inputStringDialogBuilder = new InputDialogBuilder(this, InputDialog.STRING);

    EditImageText eit;
    DiaryEntry entry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_editor);

        entry = new DiaryEntry("My Diary Entry");

        setTitle(entry.getTitle());

        eit = findViewById(R.id.editImageText);
        eit.setText(entry.getContent());
    }

    // create an action bar button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.diary_entry_menu, menu);

        //Mood
        menu.findItem(R.id.action_mood).setTitle(entry.getMood().toString());

        //Location
        if(entry.getLocation() == null){
            menu.findItem(R.id.action_location).setTitle(R.string.action_location_set_text);
        }
        else{
            menu.findItem(R.id.action_location).setTitle(R.string.action_location_change_text);
        }

        //Date
        if(entry.getDate() == null){
            menu.findItem(R.id.action_date).setTitle(R.string.action_date_set_text);
        }
        else{
            menu.findItem(R.id.action_date).setTitle(R.string.action_date_change_text);
        }

        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_mood) {
            entry.setMood(entry.getMood().getNext());
            item.setTitle(entry.getMood().toString());
        }
        else if(id == R.id.action_title){
            inputStringDialogBuilder.reset()
                    .setTitle("Enter Title")
                    .setPositiveButton(object -> {
                        String string = (String) object;
                        entry.setTitle(string);
                        setTitle(string);
                    })
                    .setNegativeButton(null)
                    .create().show();
        }

        return super.onOptionsItemSelected(item);
    }
}