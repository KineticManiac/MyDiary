package com.example.mydiary.register;

import com.example.mydiary.other.SizedInputStream;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

public interface Registry extends Serializable {
    Registry DEFAULT = MemoryRegistry.SINGLETON;

    SizedInputStream getInputStream(String register);
    OutputStream getOutputStream(String register);
}
