package com.api.booklog.response;

import com.api.booklog.domain.Post;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

/**
 * 서비스 정책을 적용할 응답 클래스
 */
@Slf4j
@Getter
public class PostResponse {
    private final Long userId;
    private final Long postId;
    private final String title;
    private final String content;
    private final LocalDateTime regDate;

    // 생성자 오버로딩
    public PostResponse(Post post) {
        this.userId = post.getUserId();
        this.postId = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.regDate = post.getRegDate();
    }

}
