package com.api.booklog.exception;

public class AlreadyExistUserInformation extends RootException{
    private static final String MESSAGE = "이미 사용 중인 이메일 또는 이름입니다.";
    public AlreadyExistUserInformation() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
