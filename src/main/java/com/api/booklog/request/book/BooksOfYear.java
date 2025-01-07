package com.api.booklog.request.book;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BooksOfYear {
    public Long bookId;
    private String review; // 서평
}
