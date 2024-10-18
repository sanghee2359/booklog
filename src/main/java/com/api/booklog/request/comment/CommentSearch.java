package com.api.booklog.request.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static java.lang.Math.max;
import static java.lang.Math.min;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class CommentSearch {

    private static final int MAX_SIZE = 2000;
    @Builder.Default
    private int page = 1;

    @Builder.Default
    private int size = 5;

    public long getOffSet() {
        return (long) (max(1, page) - 1) * min(this.size, MAX_SIZE);
    }
    public Pageable getPageable() {
        return PageRequest.of(page - 1, size);
    }
}
