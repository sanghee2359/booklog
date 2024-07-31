package com.api.booklog.controller;

import com.api.booklog.config.AppConfig;
import com.api.booklog.request.auth.SignUp;
import com.api.booklog.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;
    private final AppConfig config;

    @GetMapping("/auth/login")
    public String login() {
        return "로그인 페이지입니다.";
    }
    @PostMapping("/auth/signup")
    public void signup(@RequestBody SignUp signUp) {
        authService.signUp(signUp);
    }


}
