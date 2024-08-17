package com.api.booklog.controller;

import com.api.booklog.response.BookMarkResponse;
import com.api.booklog.response.UserResponse;
import com.api.booklog.service.BookMarkService;
import com.api.booklog.config.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BookMarkController {
    private BookMarkService bookmarkService;
    @PostMapping("users/bookmarks/add")
    public void addBookmark(@RequestParam Long postId, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        bookmarkService.addBookmark(userPrincipal.getUserId(), postId);
    }

    @GetMapping("users/bookmarks")
    public ResponseEntity<BookMarkResponse> getBookmarks(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        if(userPrincipal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        BookMarkResponse response = bookmarkService.getBookmarks( userPrincipal.getUserId() );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("users/bookmarks/remove")
    public void removeBookmark(@RequestParam Long postId, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        bookmarkService.removeBookmark(userPrincipal.getUserId(), postId);
    }

}
