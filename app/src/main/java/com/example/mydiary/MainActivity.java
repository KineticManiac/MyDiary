package com.example.mydiary;

import android.os.Bundle;
import android.text.style.ImageSpan;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText et = findViewById(R.id.editTextTextMultiLine4);
        et.setText("Hello  World! Hi  Mom!");

        ImageSpan imageSpan1 = new ImageSpan(this, R.drawable.icon);
        ImageSpan imageSpan2 = new ImageSpan(this, R.drawable.icon);

        ImageAttacher.attach(et, imageSpan1, 6);
        ImageAttacher.attach(et, imageSpan2, 18);
    }
}