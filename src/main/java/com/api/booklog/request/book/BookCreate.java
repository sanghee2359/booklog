package com.api.booklog.request.book;

import com.api.booklog.domain.Book;
import com.api.booklog.domain.BookStatus;
import com.api.booklog.domain.UserEntity;
import lombok.*;

import java.time.LocalDate;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BookCreate {

    private String title;

    private String author;

    private BookStatus status;

    private LocalDate startDate;

    //  객체 변환
    @Builder
    public Book toEntity(UserEntity user) {
        return Book.builder()
                .title(this.title)
                .author(this.author)
                .user(user)
                .status(this.status)
                .startDate(this.startDate)
                .build();
    }
}