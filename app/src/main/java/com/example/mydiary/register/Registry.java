package com.example.mydiary.register;

import com.example.mydiary.other.SizedInputStream;

import java.io.OutputStream;
import java.io.Serializable;

public interface Registry extends Serializable {
    SizedInputStream getInputStream(String register);
    OutputStream getOutputStream(String register);
    void removeRegister(String register);
}
