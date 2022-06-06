package com.example.mydiary.other;

import java.io.IOException;
import java.io.InputStream;

public abstract class SizedInputStream extends InputStream {
    public abstract int size();
    public byte[] readAll() throws IOException {
        final int size = size();
        final byte[] bytes = new byte[size];
        for(int i = 0; i < size; i++){
            int data = read();
            assert data != -1;
            bytes[i] = (byte) data;
        }
        assert read() == -1;
        close();
        return bytes;
    }
}
