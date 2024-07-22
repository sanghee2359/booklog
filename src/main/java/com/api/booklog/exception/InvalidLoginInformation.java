package com.api.booklog.exception;

import lombok.Getter;

@Getter
public class InvalidLoginInformation extends RootException{
    private static final String MESSAGE = "아이디/비밀번호가 올바르지 않습니다.";
    public String fieldName;
    public String validMessage;
    public InvalidLoginInformation() {
        super(MESSAGE);
    }
    public InvalidLoginInformation(String fieldName, String validMessage) {
        super(MESSAGE);
        addValidation(fieldName, validMessage);

    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
