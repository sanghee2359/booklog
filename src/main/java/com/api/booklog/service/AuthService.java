package com.api.booklog.service;

import com.api.booklog.crypto.ScryptPasswordEncoder;
import com.api.booklog.domain.Users;
import com.api.booklog.exception.AlreadyExistEmail;
import com.api.booklog.repository.UserRepository;
import com.api.booklog.request.auth.SignUp;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;


    public void signUp(SignUp signUp) {
        Optional<Users> usersOptional = userRepository.findByEmail(signUp.getEmail());
        if(usersOptional.isPresent()) {
            throw new AlreadyExistEmail();
        }
        ScryptPasswordEncoder encoder = new ScryptPasswordEncoder();
        Users user = Users.builder()
                .name(signUp.getName())
                .email(signUp.getEmail())
                .password(encoder.encrypt(signUp.getPassword())) // 암호화
                .build();
        userRepository.save(user);
    }
}
