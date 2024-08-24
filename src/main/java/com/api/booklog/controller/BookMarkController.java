package com.api.booklog.controller;

import com.api.booklog.config.UserPrincipal;
import com.api.booklog.exception.Unauthorized;
import com.api.booklog.response.BookmarkResponse;
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

    @GetMapping("/users/bookmarks")
    public ResponseEntity<PagingResponse<PostResponse>> getBookmarks(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size){
        if(userPrincipal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        PagingResponse<PostResponse> response = bookmarkService.getBookmarks( userPrincipal.getUserId(), page, size );
        return ResponseEntity.ok(response);
    }
    // 북마크 토글

    @PostMapping("/bookmarks/{postId}")
    public ResponseEntity<BookmarkResponse> toggleBookmark(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        if (userPrincipal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long userId = userPrincipal.getUserId();

        BookmarkResponse response = bookmarkService.toggleBookmark(userId, postId);
        // 로그로 응답 확인
        System.out.println("Response: " + response);

        return ResponseEntity.ok(response);
    }
    // 현재 북마크 상태 체크
    @GetMapping("/users/bookmarks/{postId}")
    public boolean checkBookmarkStatus(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) {
            throw new Unauthorized();
        }

        return bookmarkService.isMemberOfZSet(bookmarkService.makeKey(
                        userPrincipal.getUserId()),
                        postId);
    }

}
