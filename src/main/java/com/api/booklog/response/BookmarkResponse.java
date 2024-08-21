package com.api.booklog.response;

import lombok.Getter;


@Getter
public class BookmarkResponse {
    private boolean success;
    private boolean newStatus;

    public BookmarkResponse(boolean success, boolean newStatus) {
        this.success = success;
        this.newStatus = newStatus;
    }

}
