package com.example.mydiary.register;

import android.content.Context;

import com.example.mydiary.other.SizedInputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class FileRegistry implements Registry{

    private final File filesDir;

    public FileRegistry(Context context){
        filesDir = context.getFilesDir();
    }

    @Override
    public SizedInputStream getInputStream(String register) {
        File file = new File(filesDir, register);
        try {
            FileInputStream fis = new FileInputStream(file);
            return new SizedInputStream() {
                @Override
                public int size() {
                    return (int) file.length();
                }

                @Override
                public int read() throws IOException {
                    return fis.read();
                }

                @Override
                public void close() throws IOException{
                    fis.close();
                }
            };
        }
        catch (FileNotFoundException e){
            return null;
        }
    }

    @Override
    public OutputStream getOutputStream(String register) {
        File file = new File(filesDir, register);

        try {
            file.createNewFile();
            return new FileOutputStream(file);
        }
        catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeRegister(String register) {
        File file = new File(filesDir, register);
        file.delete();
    }
}
