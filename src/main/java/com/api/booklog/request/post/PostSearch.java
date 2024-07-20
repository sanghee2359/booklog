package com.api.booklog.request.post;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import static java.lang.Math.*;

@Getter
@Setter
@Builder
public class PostSearch {
    private static final int MAX_SIZE = 2000;
    @Builder.Default
    private int page = 1;

    @Builder.Default
    private int size = 10;

    public long getOffSet() {
        return (long) (max(1, page) - 1) * min(this.size, MAX_SIZE);
    }
}
