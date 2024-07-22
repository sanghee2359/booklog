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
        Users user = userRepository.findByEmailAndPassword(login.getEmail(), login.getPassword())
                .orElseThrow(InvalidLoginInformation::new);
        // 로그인 처리가 잘 되면 세션 생성
//        Session session = user.addSession();
        return user.getId();
    }

    public void signUp(SignUp signUp) {
        Optional<Users> usersOptional = userRepository.findByEmail(signUp.getEmail());
        if(usersOptional.isPresent()) {
            throw new AlreadyExistEmail();
        }
        Users user = Users.builder()
                .name(signUp.getName())
                .email(signUp.getEmail())
                .password(signUp.getPassword())
                .build();
        userRepository.save(user);
    }
}
