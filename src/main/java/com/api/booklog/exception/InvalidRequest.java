package com.api.booklog.exception;

import lombok.Getter;

@Getter
public class InvalidRequest extends RootException{
    private static final String MESSAGE = "잘못된 요청입니다.";
    public String fieldName;
    public String validMessage;
    public InvalidRequest() {
        super(MESSAGE);
    }
    public InvalidRequest(String fieldName, String validMessage) {
        super(MESSAGE);
        addValidation(fieldName, validMessage);
    }
    @Override
    public int getStatusCode() {
        return 400;
    }
}
