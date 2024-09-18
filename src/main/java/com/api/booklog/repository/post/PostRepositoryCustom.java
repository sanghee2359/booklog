package com.api.booklog.repository.post;

import com.api.booklog.domain.Post;
import com.api.booklog.request.post.PostSearch;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepositoryCustom {
    Page<Post> getList(PostSearch postSearch);

    // 북마크된 게시글 가져오기 - 최신 북마크한 순서가 마지막이 되도록 정렬
    List<Post> findPostsByIdsOrderByIdCustom(List<Long> ids);

    Page<Post> getListByUser(Long userId, PostSearch postSearch);
}
