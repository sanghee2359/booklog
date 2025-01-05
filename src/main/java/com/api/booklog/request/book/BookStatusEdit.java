package com.api.booklog.request.book;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookStatusEdit {
    public Long bookId;
    private LocalDate startDate;
    private LocalDate endDate;
}
