package com.api.booklog.service;

import com.api.booklog.domain.Session;
import com.api.booklog.domain.Users;
import com.api.booklog.exception.InvalidLoginInformation;
import com.api.booklog.repository.UserRepository;
import com.api.booklog.request.Login;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;

    @Transactional
    public String signIn(Login login) {
        login.validate();
        Users user = userRepository.findByEmailAndPassword(login.getEmail(), login.getPassword())
                .orElseThrow(InvalidLoginInformation::new);
        // 로그인 처리가 잘 되면 세션 생성
        Session session = user.addSession();
        return session.getAccessToken();
    }

}
