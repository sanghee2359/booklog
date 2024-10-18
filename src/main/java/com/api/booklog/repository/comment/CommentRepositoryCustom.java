package com.api.booklog.repository.comment;

import com.api.booklog.domain.Comment;
import com.api.booklog.request.comment.CommentSearch;
import org.springframework.data.domain.Page;

public interface CommentRepositoryCustom {
    Page<Comment> getCommentListByPost(Long postId, CommentSearch commentSearch);

}
