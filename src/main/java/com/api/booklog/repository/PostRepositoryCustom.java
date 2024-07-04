package com.api.booklog.repository;

import com.api.booklog.domain.Post;
import com.api.booklog.request.PostSearch;

import java.util.List;

public interface PostRepositoryCustom {
    List<Post> getList(PostSearch postSearch);
}
