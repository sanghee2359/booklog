package com.api.booklog.repository;

import com.api.booklog.domain.Likes;
import com.api.booklog.domain.Post;
import com.api.booklog.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikesRepository extends JpaRepository<Likes, Long> {
    Optional<Likes> findByPostAndUser(Post post, Users user);
    boolean existsByPostAndUser(Post post, Users user);
}
