package com.api.booklog.repository;


import com.api.booklog.domain.Book;
import com.api.booklog.domain.BookStatus;
import com.api.booklog.domain.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    // 완독한 책 리스트
    Page<Book> findByStatusAndUser(BookStatus status, Users user, Pageable pageable);

    // 읽을 책 리스트
    Page<Book> findByStatusInAndUser(List<BookStatus> statuses, Users user, Pageable pageable);

}