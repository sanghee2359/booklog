package com.api.booklog.request.book;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookEdit {
    public Long bookId;
    private LocalDate startDate;
    public LocalDate endDate;
    public String review; // endDate 설정 후 review 등록
    public boolean isYearBook; // 올해의 책 선정 여부
}
