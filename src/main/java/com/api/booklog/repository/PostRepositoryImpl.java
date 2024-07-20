package com.api.booklog.repository;

import com.api.booklog.domain.Post;
import com.api.booklog.domain.QPost;
import com.api.booklog.request.post.PostSearch;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public List<Post> getList(PostSearch postSearch) {
        return jpaQueryFactory.selectFrom(QPost.post)
                .limit(postSearch.getSize())
                .offset(postSearch.getOffSet())
                .orderBy(QPost.post.id.desc()) // 최신 작성 글부터 먼저 나오도록
                .fetch();
    }
    // 10개만 페이징 처리가 되는 쿼리
}
