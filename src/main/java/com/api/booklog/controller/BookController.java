package com.api.booklog.controller;

import com.api.booklog.config.UserPrincipal;
import com.api.booklog.request.book.BookCreate;
import com.api.booklog.request.book.BookEdit;
import com.api.booklog.response.BookResponse;
import com.api.booklog.response.PagingResponse;
import com.api.booklog.service.BookListService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@Slf4j
@RestController
@RequiredArgsConstructor
public class BookController {
    private final BookListService bookListService;
    // 책 등록
    @PostMapping("/users/bookList")
    public void saveBook(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody @Valid BookCreate request){
        bookListService.saveBook(userPrincipal.getUserId(), request);
    }
    // 모든 읽을 책 리스트
    @GetMapping("/users/bookList/pending")
    public ResponseEntity<PagingResponse<BookResponse>> getPendingBooks(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size){
        if(userPrincipal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        PagingResponse<BookResponse> response = bookListService.getPendingBooks(userPrincipal.getUserId(), page, size );
        return ResponseEntity.ok(response);
    }
    // 읽을 책 리스트
    @GetMapping("/users/bookList/completed")
    public ResponseEntity<PagingResponse<BookResponse>> getCompletedBooks(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size){
        if(userPrincipal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        PagingResponse<BookResponse> response = bookListService.getCompletedBooks(userPrincipal.getUserId(), page, size );
        return ResponseEntity.ok(response);
    }

    // 읽을 책 리스트에서 삭제
    @DeleteMapping("/users/bookList/{bookId}")
    public ResponseEntity<Void> deleteBook(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long bookId){
        bookListService.deleteBook(userPrincipal.getUserId(), bookId);
        return ResponseEntity.ok().build();
    }

    // BookStatus 변경
    @PatchMapping("/users/bookList")
    public ResponseEntity<Void> bookStatusEdit(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody @Valid BookEdit request){
        if(userPrincipal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        bookListService.updateBookStatus(userPrincipal.getUserId(), request);
        return ResponseEntity.ok().build();
    }

    // 올해의 책 리스트 출력(최대 10개만 저장가능)
    @GetMapping("/users/year-books/{year}")
    public ResponseEntity<List<BookResponse>> getBooksOfYear(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable int year) {
        List<BookResponse> response = bookListService.getThisYearBooks(userPrincipal.getUserId(), year);
        return ResponseEntity.ok(response);
    }

}
