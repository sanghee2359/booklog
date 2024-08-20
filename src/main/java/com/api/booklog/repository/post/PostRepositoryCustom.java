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
//    @Query(value = "SELECT * FROM post WHERE id IN :ids ORDER BY FIELD(id, :ids)", nativeQuery = true)
    List<Post> findPostsByIdsOrderByIdCustom(List<Long> ids);
}
