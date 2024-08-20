package com.api.booklog.response;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

@Getter
public class PagingResponse<T> {
    private final long page;
    private final long size;
    private final long totalCount;
    private final List<T> items;
    // 무한 스크롤
    private boolean hasNextPage;
    public PagingResponse(Page<?> page, Class<T> dtoClass) { //, Class<T> classes
        this.page = page.getNumber() + 1;
        this.size = page.getSize();
        this.totalCount = page.getTotalElements();
        this.items = page.getContent()
                .stream()
                .map(content -> {
                    try {
                        // entity 정보를 받아오고, dto 인스턴스화 시킴 (content = post), (classes<T> = postResponse)
                        return dtoClass.getConstructor(content.getClass()).newInstance(content);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();
    }

    public void setHasNextPage(boolean hasNextPage) {
        this.hasNextPage = hasNextPage;
    }
}
