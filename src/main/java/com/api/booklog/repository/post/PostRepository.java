package com.api.booklog.repository.post;

import com.api.booklog.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post,Long>, PostRepositoryCustom {
}
