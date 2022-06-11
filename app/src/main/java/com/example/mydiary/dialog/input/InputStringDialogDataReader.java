package com.example.mydiary.dialog.input;

import android.view.View;
import android.widget.EditText;

import com.example.mydiary.R;

public class InputStringDialogDataReader implements InputDialog.DataReader{
    public static InputStringDialogDataReader SINGLETON = new InputStringDialogDataReader();

    private InputStringDialogDataReader(){} //Constructor private olmalı. Singleton çünkü.

    @Override
    public Object getData(View view) {
        EditText editText = view.findViewById(R.id.inputStringEditText);
        return editText.getText().toString();
    }
}
