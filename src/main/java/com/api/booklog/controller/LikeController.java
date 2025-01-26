package com.api.booklog.controller;

import com.api.booklog.response.LikeResponse;
import com.api.booklog.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;

    // 사용자가 좋아요를 눌렀는지 확인
    @GetMapping("/posts/{postId}/like")
    public ResponseEntity<LikeResponse> getLikeStatus(
            Authentication authentication ,
            @PathVariable Long postId) {
        if(authentication == null)  return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        LikeResponse response = likeService.getLikeStatus(postId, authentication.getName());
        return ResponseEntity.ok(response);
    }

    // 좋아요 토글
    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<LikeResponse> toggleLike(
            Authentication authentication,
            @PathVariable Long postId){
        if(authentication == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        LikeResponse response = likeService.toggleLike(postId, authentication.getName());
        return ResponseEntity.ok(response);
    }
}
