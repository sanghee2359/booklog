package com.api.booklog.controller;

import com.api.booklog.config.UserPrincipal;
import com.api.booklog.response.LikeResponse;
import com.api.booklog.response.PostResponse;
import com.api.booklog.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;

    // 사용자가 좋아요를 눌렀는지 확인
    @GetMapping("/posts/{postId}/like")
    public ResponseEntity<LikeResponse> getLikeStatus(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        Long userId = (userPrincipal != null) ? userPrincipal.getUserId() : null;
        LikeResponse response = likeService.getLikeStatus(postId, userId);
        return ResponseEntity.ok(response);
    }

    // 좋아요 토글
    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<LikeResponse> toggleLike(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserPrincipal userPrincipal){
        Long userId = userPrincipal.getUserId();
        LikeResponse response = likeService.toggleLike(postId, userId);
        return ResponseEntity.ok(response);
    }
}
