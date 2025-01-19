package com.api.booklog.exception;

import lombok.Getter;

@Getter
public class InvalidRefreshToken extends RootException{
    private static final String MESSAGE = "잘못된 토큰입니다.";
    public String fieldName;
    public String validMessage;
    public InvalidRefreshToken() {
        super(MESSAGE);
    }
    public InvalidRefreshToken(String fieldName, String validMessage) {
        super(MESSAGE);
        addValidation(fieldName, validMessage);
    }
    @Override
    public int getStatusCode() {
        return 400;
    }
}
