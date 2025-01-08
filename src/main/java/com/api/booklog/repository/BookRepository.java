package com.api.booklog.repository;


import com.api.booklog.domain.Book;
import com.api.booklog.domain.BookStatus;
import com.api.booklog.domain.Users;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    /**
     * 올해의 책
     */
    @Query("SELECT COUNT(b) FROM Book b WHERE b.user.id = :userId AND b.isYearBook = true AND YEAR(b.endDate) = :year")
    long countYearBooksByUserAndYear(@Param("userId") Long userId, @Param("year") int year);
    // (year)에 해당되는, 특정 사용자의 올해의 책 리스트 가져오기
    @Query("SELECT b FROM Book b WHERE b.user.id = :userId AND b.isYearBook = true AND YEAR(b.endDate) = :year")
    List<Book> findYearBooksByUserAndYear(@Param("userId") Long userId, @Param("year") int year);
    void deleteByIsYearBookAndUserIdAndId(boolean isYearBook, Long userId, Long bookId);

    // 읽을 책 리스트
    Page<Book> findByStatusInAndUser(List<BookStatus> statuses, Users user, Pageable pageable);

    // 완독한 리스트
    Page<Book> findByStatusAndUser(BookStatus status, Users user, Pageable pageable);

}