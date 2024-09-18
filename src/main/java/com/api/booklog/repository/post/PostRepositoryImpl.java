package com.api.booklog.repository.post;

import com.api.booklog.domain.Post;
import com.api.booklog.request.post.PostSearch;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

import static com.api.booklog.domain.QPost.post;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom{
    @PersistenceContext
    private final EntityManager entityManager;
    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public Page<Post> getList(PostSearch postSearch) {
        long totalCount = jpaQueryFactory.select(post.count())
                .from(post)
                .fetchFirst();

        List<Post> items =  jpaQueryFactory.selectFrom(post)
                .limit(postSearch.getSize())
                .offset(postSearch.getOffSet())
                .orderBy(post.id.desc()) // 최신 작성 글부터 먼저 나오도록
                .fetch();
        return new PageImpl<>(items, postSearch.getPageable(), totalCount);
    }

    @Override
    public List findPostsByIdsOrderByIdCustom(List<Long> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }

        // 동적으로 쿼리 생성
        StringBuilder queryStr = new StringBuilder("SELECT * FROM post WHERE id IN (");
        for (int i = 0; i < ids.size(); i++) {
            queryStr.append("?");
            if (i < ids.size() - 1) {
                queryStr.append(",");
            }
        }
        queryStr.append(") ORDER BY FIELD(id, ");
        for (int i = 0; i < ids.size(); i++) {
            queryStr.append("?");
            if (i < ids.size() - 1) {
                queryStr.append(",");
            }
        }
        queryStr.append(")");

        Query query = entityManager.createNativeQuery(queryStr.toString(), Post.class);

        // 파라미터 설정
        int index = 1;
        for (Long id : ids) {
            query.setParameter(index++, id);
        }
        for (Long id : ids) {
            query.setParameter(index++, id);
        }

        return query.getResultList();
    }

    @Override
    public Page<Post> getListByUser(Long userId, PostSearch postSearch) {
        long totalCount = jpaQueryFactory
                .select(post.count())
                .from(post)
                .where(post.user.id.eq(userId))
                .fetchFirst();
        // 사용자에 맞는 게시글 리스트
        List<Post> items = jpaQueryFactory
                .selectFrom(post)
                .where(post.user.id.eq(userId))
                .limit(postSearch.getSize())
                .offset(postSearch.getOffSet())
                .orderBy(post.id.desc())
                .fetch();
        return new PageImpl<>(items, postSearch.getPageable(), totalCount);
    }


}
