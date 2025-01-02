package com.api.booklog.exception;

import lombok.Getter;

@Getter
public class InvalidBookStatus extends RootException{
    private static final String MESSAGE = "이미 완료된 책입니다.";

    public InvalidBookStatus() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 409;
    }
}
