package com.api.booklog.repository.post;

import com.api.booklog.domain.Post;
import com.api.booklog.domain.QPost;
import com.api.booklog.request.post.PostSearch;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;

import java.util.List;

import static com.api.booklog.domain.QPost.post;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public PageImpl<Post> getList(PostSearch postSearch) {
        long totalCount = jpaQueryFactory.select(post.count())
                .from(post)
                .fetchFirst();
        List<Post> items =  jpaQueryFactory.selectFrom(post)
                .limit(postSearch.getSize())
                .offset(postSearch.getOffSet())
                .orderBy(post.id.desc()) // 최신 작성 글부터 먼저 나오도록
                .fetch();
        return new PageImpl(items, postSearch.getPageable(), totalCount);
    }
    // 10개만 페이징 처리가 되는 쿼리
}
