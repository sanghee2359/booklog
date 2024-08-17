package com.api.booklog.exception;

public class BookmarkNotFound extends RootException{

    private static final String MESSAGE = "북마크에 존재하지 않는 게시글입니다.";
    public BookmarkNotFound() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 404;
    }
}
