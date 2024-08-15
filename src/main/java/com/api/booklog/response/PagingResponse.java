package com.api.booklog.response;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class PagingResponse<T> {
    private final long page;
    private final long size;
    private final long totalCount;
    private final List<T> items;
    public PagingResponse(Page<?> page, Class<T> classes) {
        this.page = page.getNumber();
        this.size = page.getSize();
        this.totalCount = page.getTotalElements();
        this.items = page.getContent().stream()
                .map(content -> {
                    try {
                        // entity의 정보를 받아오고, 인스턴스화 시킴 (content = post), (classes<T> = postResponse)
                        return classes.getConstructor(content.getClass()).newInstance(content);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();
    }
}
