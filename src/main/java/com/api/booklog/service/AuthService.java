package com.api.booklog.service;

import com.api.booklog.domain.Session;
import com.api.booklog.domain.Users;
import com.api.booklog.exception.AlreadyExistEmail;
import com.api.booklog.exception.InvalidLoginInformation;
import com.api.booklog.exception.InvalidRequest;
import com.api.booklog.exception.Unauthorized;
import com.api.booklog.repository.UserRepository;
import com.api.booklog.request.auth.Login;
import com.api.booklog.request.auth.SignUp;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.rmi.AlreadyBoundException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;

    @Transactional
    public Long signIn(Login login) {
//        Users user = userRepository.findByEmailAndPassword(login.getEmail(), login.getPassword())
//                .orElseThrow(InvalidLoginInformation::new);
        Users user = userRepository.findByEmail(login.getEmail())
                .orElseThrow(InvalidLoginInformation::new);

        SCryptPasswordEncoder encoder = new SCryptPasswordEncoder(16, 8, 1, 32, 64);
        var matches = encoder.matches(login.getPassword(), user.getPassword());
        if(!matches) throw new InvalidLoginInformation();

        return user.getId();
    }

    public void signUp(SignUp signUp) {
        Optional<Users> usersOptional = userRepository.findByEmail(signUp.getEmail());
        if(usersOptional.isPresent()) {
            throw new AlreadyExistEmail();
        }

        SCryptPasswordEncoder encoder = new SCryptPasswordEncoder(16, 8, 1, 32, 64);
        String encryptedPass = encoder.encode(signUp.getPassword());
        Users user = Users.builder()
                .name(signUp.getName())
                .email(signUp.getEmail())
                .password(encryptedPass) // 암호화
                .build();
        userRepository.save(user);
    }
}
