package com.api.booklog.response;

import com.api.booklog.domain.Comment;
import io.micrometer.common.lang.Nullable;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
@Getter
public class CommentResponse {
    @Nullable
    private final Long userId;
    private final Long commentId;
    private final Long postId;
    private final String author;
    private final String content;
    private final LocalDateTime regDate;

    // 생성자 오버로딩
    public CommentResponse(Comment comment) {
        this.userId = (comment.getUser() != null)? comment.getUser().getId() : null;
        this.commentId = comment.getId();
        this.postId = comment.getPost().getId();
        this.author = comment.getAuthor();
        this.content = comment.getContent();
        this.regDate = comment.getRegDate();
    }
}
