package com.api.booklog.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class BookMarkResponse {
    private final Long userId;
    private final List<PostResponse> bookmarks;
}
