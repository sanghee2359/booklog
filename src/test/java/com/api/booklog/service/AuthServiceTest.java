package com.api.booklog.service;

import com.api.booklog.domain.Users;
import com.api.booklog.repository.UserRepository;
import com.api.booklog.request.auth.SignUp;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class AuthServiceTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthService authService;
    @BeforeEach
    void clean() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("회원가입 성공")
    void signUp_success() {
        // given
        SignUp signUp = SignUp.builder()
                .name("정상희")
                .password("1234")
                .email("wjdtkdgml7352@naver.com").
                build();
        // when
        authService.signUp(signUp);
        // then
        Assertions.assertEquals(1, userRepository.count());
        // DB 저장된 내용 검증
        Users user = userRepository.findAll().iterator().next();
        assertEquals("wjdtkdgml7352@naver.com", user.getEmail());
        assertEquals("1234", user.getPassword());
    }
}