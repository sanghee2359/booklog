package com.api.booklog.exception;

import lombok.Getter;

@Getter
public class InvalidBookStatus extends RootException{
    private static final String MESSAGE = "책의 상태가 올바르지 않습니다.";

    public InvalidBookStatus() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
