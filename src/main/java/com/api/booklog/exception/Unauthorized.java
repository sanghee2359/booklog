package com.api.booklog.exception;

public class Unauthorized extends RootException {
    private static final String MESSAGE = "권한이 없습니다.";
    public Unauthorized() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 401;
    }
}
