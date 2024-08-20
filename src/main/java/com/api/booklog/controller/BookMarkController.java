package com.api.booklog.controller;

import com.api.booklog.config.UserPrincipal;
import com.api.booklog.response.PagingResponse;
import com.api.booklog.response.PostResponse;
import com.api.booklog.service.BookMarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class BookMarkController {
    private final BookMarkService bookmarkService;
    @PostMapping("/users/bookmarks/{postId}")
    public void addBookmark(@PathVariable Long postId, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        bookmarkService.addBookmark(userPrincipal.getUserId(), postId);
    }

    @GetMapping("/users/bookmarks")
    public ResponseEntity<PagingResponse<PostResponse>> getBookmarks(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size){
        if(userPrincipal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        PagingResponse<PostResponse> response = bookmarkService.getBookmarks( userPrincipal.getUserId(), page, size );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/users/bookmarks/{postId}")
    public void removeBookmark(@PathVariable Long postId, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        bookmarkService.removeBookmark(userPrincipal.getUserId(), postId);
    }

}
