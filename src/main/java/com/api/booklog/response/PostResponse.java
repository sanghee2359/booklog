package com.api.booklog.response;

import com.api.booklog.domain.Post;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 서비스 정책을 적용할 응답 클래스
 */
@Slf4j
@Getter
public class PostResponse {
    private final Long id;
    private final String title;
    private final String content;

    // 생성자 오버로딩
    public PostResponse(Post post ) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
    }
    @Builder
    public PostResponse(Long id, String title, String content) {
        this.id = id;
        this.title = title.substring(0, Math.min(title.length(), 10)); // 글자 수 제한
        this.content = content;
    }
}
