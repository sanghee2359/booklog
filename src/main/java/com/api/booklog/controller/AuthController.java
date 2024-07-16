package com.api.booklog.controller;

import com.api.booklog.domain.Users;
import com.api.booklog.exception.InvalidLoginInformation;
import com.api.booklog.repository.UserRepository;
import com.api.booklog.request.Login;
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
    private final UserRepository userRepository;
    @PostMapping("/auth/login")
    public Users login(@RequestBody @Valid Login login) {
//        log.info(">>login : {}", login.toString());
        login.validate();
        Users user = userRepository.findByEmailAndPassword(login.getEmail(), login.getPassword())
                .orElseThrow(InvalidLoginInformation::new);
        return user;
    }
}
