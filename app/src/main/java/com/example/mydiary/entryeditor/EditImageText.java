package com.example.mydiary.entryeditor;

import android.content.ClipDescription;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.os.BuildCompat;
import androidx.core.view.inputmethod.EditorInfoCompat;
import androidx.core.view.inputmethod.InputConnectionCompat;
import androidx.core.view.inputmethod.InputContentInfoCompat;

import java.io.InputStream;

public class EditImageText extends AppCompatEditText {
    private static final String[] MIME_TYPES =
            new String [] {"image/png", "image/bmp", "image/jpg", "image/jpeg"};

    protected final Context context;

    public EditImageText(Context context) {
        super(context);
        this.context = context;
    }

    public EditImageText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public EditImageText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo editorInfo) {
        final InputConnection ic = super.onCreateInputConnection(editorInfo);
        EditorInfoCompat.setContentMimeTypes(editorInfo, MIME_TYPES);

        final InputConnectionCompat.OnCommitContentListener callback =
                new OnCommitContentListener();
        return InputConnectionCompat.createWrapper(ic, editorInfo, callback);
    }

    private class OnCommitContentListener
            implements InputConnectionCompat.OnCommitContentListener{
        @Override
        public boolean onCommitContent(InputContentInfoCompat inputContentInfo,
                                       int flags, Bundle opts) {
            // read and display inputContentInfo asynchronously
            if (BuildCompat.isAtLeastNMR1() && (flags &
                    InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION) != 0) {
                try {
                    inputContentInfo.requestPermission();
                }
                catch (Exception e) {
                    return false; // return false if failed
                }
            }

            ClipDescription desc = inputContentInfo.getDescription();
            if(desc.getMimeTypeCount() != 1)
                return false; //Bununla uğraşmayalım

            String mime = desc.getMimeType(0);
            for(String desired : MIME_TYPES){
                if(ClipDescription.compareMimeTypes(mime, desired)){
                    InputStream is = null;
                    try {
                        is = context.getContentResolver()
                                .openInputStream(inputContentInfo.getContentUri());

                        Bitmap bmp = BitmapFactory.decodeStream(is);
                        ImageAttacher.attach(EditImageText.this, new ImageSpan(context, bmp), getSelectionStart());
                        return true;
                    }
                    catch(Exception e){
                        e.printStackTrace();
                        return false;
                    }
                    finally {
                        try{
                            if(is == null) {
                                new NullPointerException().printStackTrace(); //Bu işe yarar mı acaba?
                                return false;
                            }
                            is.close();
                        }
                        catch (Exception e){
                            e.printStackTrace();
                            return false;
                        }
                    }
                }
            }

            return false;
        }
    }
}
