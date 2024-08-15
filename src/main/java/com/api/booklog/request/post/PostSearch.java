package com.api.booklog.request.post;

import lombok.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static java.lang.Math.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class PostSearch {

    private static final int MAX_SIZE = 2000;
    @Builder.Default
    private int page = 1;

    @Builder.Default
    private int size = 10;

    public long getOffSet() {
        return (long) (max(1, page) - 1) * min(this.size, MAX_SIZE);
    }
    public Pageable getPageable() {
        return PageRequest.of(page - 1, size);
    }
}
