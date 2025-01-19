package com.api.booklog.controller;

import com.api.booklog.domain.UserEntity;
import com.api.booklog.exception.InvalidRefreshToken;
import com.api.booklog.exception.Unauthorized;
import com.api.booklog.request.auth.SignInReq;
import com.api.booklog.request.auth.SignUpReq;
import com.api.booklog.response.auth.SignedInUser;
import com.api.booklog.security.RefreshToken;
import com.api.booklog.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.*;


@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;


    @PostMapping("/api/v1/auth/token/refresh")
    public ResponseEntity<SignedInUser> getAccessToken(
            @Valid RefreshToken refreshToken) {
        return ok(authService.getAccessToken(refreshToken).orElseThrow(InvalidRefreshToken::new));
    }

    @PostMapping("/api/v1/auth/token")
    public ResponseEntity<SignedInUser> signIn(@Valid SignInReq signInReq) {
        UserEntity userEntity = authService.findUserByEmail(signInReq.getEmail());
        if (passwordEncoder.matches(signInReq.getPassword(), userEntity.getPassword())) {
            return ok(authService.getSignedInUser(userEntity));
        }
        throw new Unauthorized();
    }

    @DeleteMapping("/api/v1/auth/token")
    public ResponseEntity<Void> signOut(
            @Valid RefreshToken refreshToken) {
        // We are using removeToken API for signout.
        // Ideally you would like to get tgit she user ID from Logged in user's request
        // and remove the refresh token based on retrieved user id from request.
        authService.removeRefreshToken(refreshToken);
        return accepted().build();
    }

    @PostMapping("/api/v1/users")
    public ResponseEntity<SignedInUser> signUp(@Valid SignUpReq request) {
        // Have a validation for all required fields.
        return status(HttpStatus.CREATED).body(authService.createUser(request).get());
    }


}
