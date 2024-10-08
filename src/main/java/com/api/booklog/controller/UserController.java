package com.api.booklog.controller;

import com.api.booklog.config.UserPrincipal;
import com.api.booklog.request.UserEdit;
import com.api.booklog.response.UserResponse;
import com.api.booklog.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @GetMapping("/users/me")
    public ResponseEntity<UserResponse> getMe(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        if(userPrincipal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        UserResponse userResponse = userService.getUserProfile( userPrincipal.getUserId());
        return ResponseEntity.ok(userResponse);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/users/setting")
    public ResponseEntity<Void> edit(@AuthenticationPrincipal UserPrincipal userPrincipal
            , @RequestBody @Valid UserEdit request) {
        if(userPrincipal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        userService.edit(userPrincipal.getUserId(), request);
        return ResponseEntity.ok(null);
    }
}
