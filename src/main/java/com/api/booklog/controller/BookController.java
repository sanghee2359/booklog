package com.api.booklog.controller;

import com.api.booklog.exception.Unauthorized;
import com.api.booklog.request.book.BookCreate;
import com.api.booklog.request.book.BookEdit;
import com.api.booklog.response.BookResponse;
import com.api.booklog.response.PagingResponse;
import com.api.booklog.response.PostResponse;
import com.api.booklog.service.BookListService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
public class BookController {
    private final BookListService bookListService;
    // 책 등록
    @PostMapping("/users/bookList")
    public void saveBook(
            Authentication authentication ,
            @RequestBody @Valid BookCreate request){
        if(authentication == null) throw new Unauthorized();
        bookListService.saveBook(authentication.getName(), request);
    }
    // 책 한권 조회
    @GetMapping("/users/bookList/{bookId}")
    public BookResponse get(@PathVariable(name = "bookId") Long bookId) {
        return bookListService.get(bookId);
    }
    // 모든 읽을 책 리스트
    @GetMapping("/users/bookList/pending")
    public ResponseEntity<PagingResponse<BookResponse>> getPendingBooks(
            Authentication authentication ,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size){
        if(authentication == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        PagingResponse<BookResponse> response = bookListService.getPendingBooks(authentication.getName(), page, size );
        return ResponseEntity.ok(response);
    }
    // 완독 리스트
    @GetMapping("/users/bookList/completed")
    public ResponseEntity<PagingResponse<BookResponse>> getCompletedBooks(
            Authentication authentication ,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size){
        if(authentication == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        PagingResponse<BookResponse> response = bookListService.getCompletedBooks(authentication.getName(), page, size );
        return ResponseEntity.ok(response);
    }

    // 완독 목록 삭제
    @DeleteMapping("/users/bookList/{bookId}")
    public ResponseEntity<Void> deleteBook(
            Authentication authentication ,
            @PathVariable Long bookId){
        if(authentication == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        bookListService.deleteBook(authentication.getName(), bookId);
        return ResponseEntity.ok().build();
    }

    // BookStatus 변경
    @PatchMapping("/users/bookList")
    public ResponseEntity<Void> bookStatusEdit(
            Authentication authentication ,
            @RequestBody @Valid BookEdit request){
        if(authentication == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        bookListService.updateBookStatus(authentication.getName(), request);
        return ResponseEntity.ok().build();
    }

    // 올해의 책 리스트 출력
    @GetMapping("/users/year-books/{year}")
    public ResponseEntity<List<BookResponse>> getBooksOfYear(
            Authentication authentication ,
            @PathVariable int year) {
        if(authentication == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        List<BookResponse> response = bookListService.getThisYearBooks(authentication.getName(), year);
        return ResponseEntity.ok(response);
    }

    // 다른 사용자의 올해의 책 출력
    @GetMapping("/users/{userId}/year-books/{year}")
    public ResponseEntity<List<BookResponse>> get(
            @PathVariable(name = "userId") Long userId,
            @PathVariable int year) {

        List<BookResponse> response = bookListService.getUsersYearBooks(userId, year);
        return ResponseEntity.ok(response);

    }

}
