package com.api.booklog.response;

import com.api.booklog.domain.Book;
import com.api.booklog.domain.BookStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookResponse {
    private Long bookId;
    private String title;
    private String author;
    private BookStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime createdAt;
    private Boolean isYearBook;  // 올해의 책 여부
    private String review;       // 서평

    // 새로 추가된 생성자
    public BookResponse(Book book) {
        this.bookId = book.getId();
        this.title = book.getTitle();
        this.author = book.getAuthor();
        this.startDate = book.getStartDate();
        this.endDate = book.getEndDate();
        this.status = book.getStatus();
        this.createdAt = book.getCreatedAt();
        this.isYearBook = book.isYearBook();
        this.review = book.getReview();
    }
}
