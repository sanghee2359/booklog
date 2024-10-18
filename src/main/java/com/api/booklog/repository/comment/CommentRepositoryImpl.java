package com.api.booklog.repository.comment;

import com.api.booklog.domain.Comment;
import com.api.booklog.request.comment.CommentSearch;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

import static com.api.booklog.domain.QComment.comment;

@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    // post에 달린 comment 출력
    @Override
    public Page<Comment> getCommentListByPost (Long postId, CommentSearch commentSearch) {
        long totalCount = jpaQueryFactory.select(comment.count())
                .from(comment)
                .where(comment.post.id.eq(postId))
                .fetchFirst();

        List<Comment> items =  jpaQueryFactory.selectFrom(comment)
                .where(comment.post.id.eq(postId))
                .limit(commentSearch.getSize())
                .offset(commentSearch.getOffSet())
                .orderBy(comment.id.desc()) // 최신 작성 댓글부터 먼저 나오도록
                .fetch();
        return new PageImpl<>(items, commentSearch.getPageable(), totalCount);
    }
}