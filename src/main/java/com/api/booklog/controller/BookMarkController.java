package com.api.booklog.controller;

import com.api.booklog.exception.Unauthorized;
import com.api.booklog.response.BookmarkResponse;
import com.api.booklog.response.PagingResponse;
import com.api.booklog.response.PostResponse;
import com.api.booklog.service.BookMarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class BookMarkController {
    private final BookMarkService bookmarkService;

    @GetMapping("/users/bookmarks")
    public ResponseEntity<PagingResponse<PostResponse>> getBookmarks(
            Authentication authentication ,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size){
        if(authentication  == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        PagingResponse<PostResponse> response = bookmarkService.getBookmarks( authentication.getName(), page, size );
        return ResponseEntity.ok(response);
    }
    // 북마크 토글

    @PostMapping("/users/bookmarks/{postId}")
    public ResponseEntity<BookmarkResponse> toggleBookmark(
            Authentication authentication ,
            @PathVariable Long postId) {
        if (authentication == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        BookmarkResponse response = bookmarkService.toggleBookmark(authentication.getName(), postId);
        return ResponseEntity.ok(response);
    }
    // 현재 북마크 상태 체크
    @GetMapping("/users/bookmarks/{postId}")
    public boolean checkBookmarkStatus(
            Authentication authentication ,
            @PathVariable Long postId) {
        if (authentication == null) throw new Unauthorized();

        return bookmarkService.checkBookmarkStatus(authentication.getName(), postId);
    }

}
