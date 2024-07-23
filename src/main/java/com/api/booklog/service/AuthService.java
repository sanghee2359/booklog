package com.api.booklog.service;

import com.api.booklog.crypto.PasswordEncoder;
import com.api.booklog.domain.Users;
import com.api.booklog.exception.AlreadyExistEmail;
import com.api.booklog.exception.InvalidLoginInformation;
import com.api.booklog.repository.UserRepository;
import com.api.booklog.request.auth.Login;
import com.api.booklog.request.auth.SignUp;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; //@Component 주입

    @Transactional
    public Long signIn(Login login) {
        Users user = userRepository.findByEmail(login.getEmail())
                .orElseThrow(InvalidLoginInformation::new);

        var matches = passwordEncoder.matches(login.getPassword(), user.getPassword());
        if(!matches) throw new InvalidLoginInformation();

        return user.getId();
    }

    public void signUp(SignUp signUp) {
        Optional<Users> usersOptional = userRepository.findByEmail(signUp.getEmail());
        if(usersOptional.isPresent()) {
            throw new AlreadyExistEmail();
        }

        String encryptedPass = passwordEncoder.encrypt(signUp.getPassword());
        Users user = Users.builder()
                .name(signUp.getName())
                .email(signUp.getEmail())
                .password(encryptedPass) // 암호화
                .build();
        userRepository.save(user);
    }
}
