package com.example.mydiary.dialog.input;

import android.view.LayoutInflater;
import android.view.View;

import com.example.mydiary.R;

public enum InputDialog {
    STRING(R.layout.dialog_input_string, InputStringDialogDataReader.SINGLETON),
    PASSWORD(R.layout.dialog_input_password, InputPasswordDialogDataReader.SINGLETON);

    private final int layoutId;
    private final DataReader dataReader;

    InputDialog(int layoutId, DataReader dataReader){
        this.layoutId = layoutId;
        this.dataReader = dataReader;
    }

    public int getLayoutId() {
        return layoutId;
    }

    public View inflateView(LayoutInflater inflater){
        return inflater.inflate(layoutId, null);
    }

    public Object getData(View view){
        return dataReader.getData(view);
    }

    public interface DataReader{
        Object getData(View view);
    }
}
