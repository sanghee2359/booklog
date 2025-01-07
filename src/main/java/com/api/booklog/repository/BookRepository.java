package com.api.booklog.repository;


import com.api.booklog.domain.Book;
import com.api.booklog.domain.BookStatus;
import com.api.booklog.domain.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    /**
     * 올해의 책
     */
    long countByIsYearBookAndUserId(boolean isYearBook, Long userId);
    // 특정 사용자의 올해의 책 리스트 가져오기 (isYearBook이 true인 책만)
    List<Book> findByIsYearBookAndUserId(boolean isYearBook, Long userId);
    void deleteByIsYearBookAndUserIdAndId(boolean isYearBook, Long userId, Long bookId);

    // 읽을 책 리스트
    Page<Book> findByStatusInAndUser(List<BookStatus> statuses, Users user, Pageable pageable);

    // 완독한 리스트
    Page<Book> findByStatusAndUser(BookStatus status, Users user, Pageable pageable);

}