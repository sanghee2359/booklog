package com.api.booklog.controller;

import com.api.booklog.domain.Session;
import com.api.booklog.domain.Users;
import com.api.booklog.exception.InvalidLoginInformation;
import com.api.booklog.repository.UserRepository;
import com.api.booklog.request.Login;
import com.api.booklog.response.SessionResponse;
import com.api.booklog.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;
    @PostMapping("/auth/login")
    public SessionResponse login(@RequestBody @Valid Login login) {
        String accessToken = authService.signIn(login);
        return new SessionResponse(accessToken);
    }
}
