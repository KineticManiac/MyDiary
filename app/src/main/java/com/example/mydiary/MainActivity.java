package com.example.mydiary;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText et = findViewById(R.id.editTextTextMultiLine);
        TextView tv = findViewById(R.id.textView);

        SpannableString string = new SpannableString("Hello world!");
        string.setSpan(new ImageSpan(this, R.drawable.icon),
                0, 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        et.setText(string, TextView.BufferType.SPANNABLE);
        tv.setText(string, TextView.BufferType.SPANNABLE);
    }
}