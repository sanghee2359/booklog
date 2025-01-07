package com.api.booklog.controller;

import com.api.booklog.config.UserPrincipal;
import com.api.booklog.request.book.BookCreate;
import com.api.booklog.request.book.BookStatusEdit;
import com.api.booklog.request.book.BooksOfYear;
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
    @PostMapping("/users/{userId}/bookList")
    public void saveBook(
            @RequestBody @Valid BookCreate request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        bookListService.saveBook(userPrincipal.getUserId(), request);
    }
    // 모든 읽을 책 리스트
    @GetMapping("/users/{userId}/bookList/pending")
    public ResponseEntity<PagingResponse<BookResponse>> getPendingBooks(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size){
        if(userPrincipal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        PagingResponse<BookResponse> response = bookListService.getPendingBooks(userPrincipal.getUserId(), page, size );
        return ResponseEntity.ok(response);
    }
    // 읽을 책 리스트
    @GetMapping("/users/{userId}/bookList/completed")
    public ResponseEntity<PagingResponse<BookResponse>> getCompletedBooks(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size){
        if(userPrincipal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        PagingResponse<BookResponse> response = bookListService.getCompletedBooks(userPrincipal.getUserId(), page, size );
        return ResponseEntity.ok(response);
    }

    // 읽을 책 리스트에서 삭제
    @DeleteMapping("/users/{userId}/bookList/{bookId}")
    public ResponseEntity<Void> deleteBook(
            @PathVariable Long userId,
            @PathVariable Long bookId) {
        bookListService.deleteBook(userId, bookId);
        return ResponseEntity.ok().build();
    }

    // BookStatus 변경
    @PatchMapping("/users/{userId}/bookList")
    public ResponseEntity<Void> bookStatusEdit(
            @RequestBody @Valid BookStatusEdit request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        if(userPrincipal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        bookListService.updateBookStatus(request);
        return ResponseEntity.ok().build();
    }

    // 올해의 책 리스트 출력(최대 10개만 저장가능)
    @GetMapping("/users/{userId}/year-books/{year}")
    public ResponseEntity<List<BookResponse>> getYearBooks(
            @PathVariable Long userId,
            @PathVariable int year) {
        List<BookResponse> response = bookListService.getThisYearBooks(userId, year);
        return ResponseEntity.ok(response);
    }

    // 올해의 책 등록
    @PatchMapping("/users/{userId}/year-books")
    public ResponseEntity<Void> markAsYearBook(
            @PathVariable Long userId,
            @RequestBody BooksOfYear request) {
        bookListService.markAsYearBook(userId, request);
        return ResponseEntity.ok().build();
    }


}
