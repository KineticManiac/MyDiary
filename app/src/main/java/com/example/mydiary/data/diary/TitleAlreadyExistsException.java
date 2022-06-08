package com.example.mydiary.data.diary;

public class TitleAlreadyExistsException extends Exception {
    public TitleAlreadyExistsException() {
        super();
    }

    public TitleAlreadyExistsException(String description) {
        super(description);
    }
}
