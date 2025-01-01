package com.api.booklog.request.book;

import com.api.booklog.domain.BookStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookEdit {
    @NotNull
    private BookStatus status;
    private LocalDate date;
}
