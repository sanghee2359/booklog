package com.api.booklog.controller;

import com.api.booklog.request.UserEdit;
import com.api.booklog.response.UserResponse;
import com.api.booklog.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @GetMapping("/users/me")
    public ResponseEntity<UserResponse> getMe(Authentication authentication) {
        if(authentication == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        UserResponse userResponse = userService.getUserProfile(authentication.getName());
        return ResponseEntity.ok(userResponse);
    }
    // 포스팅한 사용자(author)의 프로필 출력
    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponse> getUserProfile(@PathVariable Long userId) {
        UserResponse userResponse = userService.getProfile(userId);
        return ResponseEntity.ok(userResponse);
    }

    @PatchMapping("/users/setting")
    public ResponseEntity<Void> edit(Authentication authentication
            , @RequestBody @Valid UserEdit request) {
        if(authentication == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        userService.edit(authentication.getName(), request);
        return ResponseEntity.ok(null);
    }

    @DeleteMapping("/users/delete")
    public ResponseEntity<Void> delete(Authentication authentication) {
        if(authentication == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        userService.softDelete(authentication.getName());
        return ResponseEntity.ok(null);
    }
}
