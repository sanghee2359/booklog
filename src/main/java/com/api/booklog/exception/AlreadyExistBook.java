package com.api.booklog.exception;

public class AlreadyExistBook extends RootException{
    private static final String MESSAGE = "이미 리스트에 등록된 책입니다.";
    public AlreadyExistBook() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
