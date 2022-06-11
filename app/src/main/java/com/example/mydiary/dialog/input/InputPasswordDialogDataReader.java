package com.example.mydiary.dialog.input;

import android.view.View;
import android.widget.EditText;

import com.example.mydiary.R;

public class InputPasswordDialogDataReader implements InputDialog.DataReader{
    public static InputPasswordDialogDataReader SINGLETON = new InputPasswordDialogDataReader();

    private InputPasswordDialogDataReader(){} //Constructor private olmalı. Singleton çünkü.

    @Override
    public Object getData(View view) {
        EditText editText = view.findViewById(R.id.inputPasswordEditText);
        return editText.getText().toString();
    }
}
