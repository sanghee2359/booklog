package com.api.booklog.controller;

import com.api.booklog.config.UserPrincipal;
import com.api.booklog.request.book.BookCreate;
import com.api.booklog.request.book.BookStatusEdit;
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


@Slf4j
@RestController
@RequiredArgsConstructor
public class BookController {
    private final BookListService bookListService;
    // 책 등록
    @PostMapping("/users/bookList")
    public void saveBook(
            @RequestBody @Valid BookCreate request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        bookListService.saveBook(userPrincipal.getUserId(), request);
    }
    // 등록된 모든 읽을 책 리스트(완독 하면 이 리스트에서 제외)
    @GetMapping("/users/pendingBookList")
    public ResponseEntity<PagingResponse<BookResponse>> getPendingBooks(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size){
        if(userPrincipal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        PagingResponse<BookResponse> response = bookListService.getPendingBooks(userPrincipal.getUserId(), page, size );
        return ResponseEntity.ok(response);
    }

    // 완독한 책 리스트만 따로 출력
    @GetMapping("/users/completedBookList")
    public ResponseEntity<PagingResponse<BookResponse>> completelyReadBookList(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size){
        if(userPrincipal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        PagingResponse<BookResponse> response = bookListService.getCompletedBooks(userPrincipal.getUserId(), page, size );
        return ResponseEntity.ok(response);

    }

    // BookStatus 변경
    @PatchMapping("/users/bookList/{bookId}")
    public ResponseEntity<Void> bookStatusEdit(
            @PathVariable Long bookId,
            @RequestBody @Valid BookStatusEdit request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        if(userPrincipal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        bookListService.updateBookStatus(bookId, request);
        return ResponseEntity.ok().build();
    }

}
