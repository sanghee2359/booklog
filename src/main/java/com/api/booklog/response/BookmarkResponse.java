package com.api.booklog.response;

import lombok.Getter;
import lombok.ToString;


@Getter
@ToString
public class BookmarkResponse {
    private final long postId;
    private boolean status;

    public BookmarkResponse(long postId, boolean newStatus) {
        this.postId = postId;
        this.status = newStatus;
    }
    public void setStatus(boolean status) {
        this.status = status;
    }

}
