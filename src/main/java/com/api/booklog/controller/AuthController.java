package com.api.booklog.controller;

import com.api.booklog.config.AppConfig;
import com.api.booklog.request.Login;
import com.api.booklog.response.SessionResponse;
import com.api.booklog.service.AuthService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.SecretKey;
import java.util.Date;


@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;
    private final AppConfig config;
    @PostMapping("/auth/login")
    public SessionResponse login(@RequestBody Login login) {
        Long userId = authService.signIn(login);
        // Key : 최초에 1번 생성
        // key 생성 -> byte형태로 전환 -> String으로 변환(java에서 제공)

//        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
//        byte[] encodedKey = key.getEncoded();
//        String strKey = Base64.getEncoder().encodeToString(encodedKey);
        SecretKey key = Keys.hmacShaKeyFor(config.getJwtKey());

        String jws = Jwts.builder()
                .setSubject(String.valueOf(userId))
                .signWith(key)
                .setIssuedAt(new Date())
                .compact();

        return new SessionResponse(jws);
    }
}
