package com.api.booklog.response;

import com.api.booklog.domain.Comment;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
@Getter
public class CommentResponse {
    private final Long commentId;
    private final Long postId;
    private final String author;
    private final String content;
    private final LocalDateTime regDate;

    // 생성자 오버로딩
    public CommentResponse(Comment comment) {
        this.commentId = comment.getId();
        this.postId = comment.getPost().getId();
        this.author = (comment.getUser() != null) ? comment.getUser().getName() : comment.getAuthor(); // 유저가 null일 경우 author 사용
        this.content = comment.getContent();
        this.regDate = comment.getRegDate();
    }
}
