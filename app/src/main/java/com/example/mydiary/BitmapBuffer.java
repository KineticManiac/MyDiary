package com.example.mydiary;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;

public class BitmapBuffer implements Serializable{
    private final byte[] buffer;

    public BitmapBuffer(Bitmap bmp){
        ByteArrayOutputStream baos;
        baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
        buffer = baos.toByteArray();
    }

    public Bitmap toBitmap(){
        ByteArrayInputStream bais;
        bais = new ByteArrayInputStream(buffer);
        return BitmapFactory.decodeStream(bais);
    }
}