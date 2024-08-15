package com.api.booklog.repository.post;

import com.api.booklog.domain.Post;
import com.api.booklog.request.post.PostSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

public interface PostRepositoryCustom {
    Page<Post> getList(PostSearch postSearch);
}
