package com.api.booklog.repository.comment;

import com.api.booklog.domain.Comment;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {
    @Modifying
    @Query("UPDATE Comment c SET c.author = '(삭제된 유저)' WHERE c.user.id = :userId")
    void updateAuthorNameToDeleted(@Param("userId") Long userId);
}
