package com.api.booklog.controller;

import com.api.booklog.domain.UserEntity;
import com.api.booklog.exception.InvalidRefreshToken;
import com.api.booklog.exception.Unauthorized;
import com.api.booklog.request.auth.SignInReq;
import com.api.booklog.request.auth.SignUpReq;
import com.api.booklog.response.auth.SignedInUser;
import com.api.booklog.security.RefreshToken;
import com.api.booklog.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import static com.api.booklog.security.config.Constants.REFRESH_TOKEN_TTL_SECONDS;
import static org.springframework.http.ResponseEntity.accepted;
import static org.springframework.http.ResponseEntity.ok;


@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;

    @CrossOrigin(origins = "http://localhost:5173", exposedHeaders = "Authorization")
    @PostMapping("/v1/auth/token/refresh")
    public ResponseEntity<Void> getAccessToken(
            @CookieValue(value = "refreshToken", defaultValue = "") String refreshToken
            ,HttpServletResponse response) {
        RefreshToken token = new RefreshToken(refreshToken);
        SignedInUser tokens = authService.getAccessToken(token).orElseThrow(InvalidRefreshToken::new);
        response.setHeader("Authorization", "Bearer " + tokens.getAccessToken());
        return ok().build();
    }

    @CrossOrigin(origins = "http://localhost:5173", exposedHeaders = "Authorization")
    @PostMapping("/v1/auth/token")
    public ResponseEntity<Void> signIn(@Valid @RequestBody SignInReq signInReq
            ,@CookieValue(value = "refreshToken", defaultValue = "") String refreshToken
            ,HttpServletResponse response) {
        UserEntity userEntity = authService.findUserByEmail(signInReq.getEmail());
        if (!passwordEncoder.matches(signInReq.getPassword(), userEntity.getPassword())) {
            throw new Unauthorized();
        }
        // RefreshToken DTO를 생성하여 서비스로 넘김
        RefreshToken token = new RefreshToken(refreshToken);
        SignedInUser tokens = authService.getSignedInUser(userEntity, token);
        // 토큰 설정
        setRefreshTokenInCookie(tokens.getRefreshToken(), response);
        response.setHeader("Authorization", "Bearer " + tokens.getAccessToken());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/v1/auth/token")
    public ResponseEntity<Void> signOut(
            @CookieValue(value = "refreshToken", defaultValue = "") String refreshToken) {
        // We are using removeToken API for signout.
        // Ideally you would like to get tgit she user ID from Logged in user's request
        // and remove the refresh token based on retrieved user id from request.
        RefreshToken token = new RefreshToken(refreshToken);
        authService.removeRefreshToken(token);
        return accepted().build();
    }

    @CrossOrigin(origins = "http://localhost:5173", exposedHeaders = "Authorization")
    @PostMapping("/v1/users")
    public ResponseEntity<Void> signUp(@Valid @RequestBody SignUpReq request, HttpServletResponse response) {
        // Have a validation for all required fields.
        SignedInUser tokens = authService.createUser(request).get();
        setRefreshTokenInCookie(tokens.getRefreshToken(), response);
        response.setHeader("Authorization",  "Bearer " + tokens.getAccessToken());

        return ResponseEntity.ok().build();
    }

    // RefreshToken을 HttpOnly 쿠키에 설정하는 메소드
    private void setRefreshTokenInCookie(String refreshToken, HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(REFRESH_TOKEN_TTL_SECONDS)  // 7일
                .sameSite("Strict")
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

}
