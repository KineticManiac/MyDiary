package com.example.mydiary.entryeditor;

import android.text.Editable;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.widget.TextView;

public class ImageAttacher {
    public static void attach(TextView textView, ImageSpan imageSpan, int where){
        Editable edit = textView.getEditableText();
        edit.insert(where, "@");
        edit.setSpan(imageSpan, where, where + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(edit);
    }
}
