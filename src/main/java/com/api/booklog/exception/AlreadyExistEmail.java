package com.api.booklog.exception;

public class AlreadyExistEmail extends RootException{
    private static final String MESSAGE = "이미 가입된 이메일 입니다.";
    public AlreadyExistEmail() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
