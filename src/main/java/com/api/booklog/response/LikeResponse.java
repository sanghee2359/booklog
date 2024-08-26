package com.api.booklog.response;

import lombok.Getter;

@Getter
public class LikeResponse {
    private final Long postId;
    private final boolean status;
    private final Long likesCount;

    public LikeResponse(Long postId, boolean newStatus, Long likesCount) {
        this.postId = postId;
        this.status = newStatus;
        this.likesCount = (likesCount != null) ? likesCount : 0L;
    }
}
