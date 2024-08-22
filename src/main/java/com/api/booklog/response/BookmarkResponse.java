package com.api.booklog.response;

import lombok.Getter;


@Getter
public class BookmarkResponse {
    private long postId;
    private boolean newStatus;

    public BookmarkResponse(long postId, boolean newStatus) {
        this.postId = postId;
        this.newStatus = newStatus;
    }

}
