package com.api.booklog.exception;

public class AlreadyBookmark extends RootException{
    private static final String MESSAGE = "이미 북마크 된 게시글입니다.";
    public AlreadyBookmark() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
