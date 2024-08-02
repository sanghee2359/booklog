package com.api.booklog.exception;

import lombok.Getter;

@Getter
public class InvalidPassword extends RootException{
    private static final String MESSAGE = "댓글의 비밀번호가 올바르지 않습니다.";

    public InvalidPassword() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
