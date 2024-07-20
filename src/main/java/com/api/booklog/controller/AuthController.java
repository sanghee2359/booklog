package com.api.booklog.controller;

import com.api.booklog.request.Login;
import com.api.booklog.response.SessionResponse;
import com.api.booklog.service.AuthService;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Key;


@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;
    @PostMapping("/auth/login")
    public SessionResponse login(@RequestBody Login login) {
        String accessToken = authService.signIn(login);
        Key key = Jwts.SIG.HS256.key().build();
        String jws = Jwts.builder().setSubject("sanghee").signWith(key).compact();
        return new SessionResponse(jws);
    }
}
