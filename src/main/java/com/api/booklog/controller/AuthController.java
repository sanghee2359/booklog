package com.api.booklog.controller;

import com.api.booklog.domain.Session;
import com.api.booklog.domain.Users;
import com.api.booklog.exception.InvalidLoginInformation;
import com.api.booklog.repository.UserRepository;
import com.api.booklog.request.Login;
import com.api.booklog.response.SessionResponse;
import com.api.booklog.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;
    @PostMapping("/auth/login")
    public ResponseEntity<Object> login(@RequestBody Login login) {
        String accessToken = authService.signIn(login);
        ResponseCookie cookie = ResponseCookie.from("SESSION", accessToken)
                .domain("localhost") // todo 서버 환경에 따른 분리  ->yml에서 설정
                .path("/")
                .httpOnly(true)
                .secure(false)
                .maxAge(Duration.ofDays(30)) // 로그인 유지 시간 = 쿠키 유지 시간 (한 달이 국룰이다)
                .sameSite("Strict")
                .build();

        log.info(">>>>>>>>>>> cookie={}", cookie.toString()); // 설정한 값이 쿠키의 형식에 맞게 문자열로 변환
        return ResponseEntity.ok() // cookie
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }
}
