package com.api.booklog.service;

import com.api.booklog.crypto.PasswordEncoder;
import com.api.booklog.crypto.ScryptPasswordEncoder;
import com.api.booklog.domain.Users;
import com.api.booklog.exception.AlreadyExistEmail;
import com.api.booklog.exception.InvalidLoginInformation;
import com.api.booklog.repository.UserRepository;
import com.api.booklog.request.auth.Login;
import com.api.booklog.request.auth.SignUp;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
@ActiveProfiles("test")
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
        assertNotNull(user.getPassword());
        assertEquals("1234", user.getPassword());
    }

    @Test
    @DisplayName("이미 존재하는 이메일은 중복 가입이 불가하다")

    void signup_email_fail() {
        // given
        ScryptPasswordEncoder encoder = new ScryptPasswordEncoder();
        userRepository.save(Users.builder()
                .name("user1")
                .email("user1@naver.com")
                .password(encoder.encrypt(encoder.encrypt("user1")))
                .build());

        SignUp signUp = SignUp.builder()
                .name("user1")
                .password("user1")
                .email("user1@naver.com").
                build();

        // expected
        assertThrows(AlreadyExistEmail.class, ()-> authService.signUp(signUp));
    }

    @Test
    @DisplayName("로그인 성공 - cryptography")
    void login_success() {
        // given
        ScryptPasswordEncoder encoder = new ScryptPasswordEncoder();
        // profile = "test"일 때는 평문 패스워드로 테스트

        userRepository.save(Users.builder()
                .name("user1")
                .email("user1@naver.com")
                .password("user1")
                .build());

        Login login = Login.builder()
                .password("user1")
                .email("user1@naver.com").
                build();

        // when
        Long userId = authService.signIn(login);

        // then
        assertNotNull(userId);
    }


    @Test
    @DisplayName("로그인 실패 - cryptography")
    void login_failed() {
        // given
        ScryptPasswordEncoder encoder = new ScryptPasswordEncoder();
        userRepository.save(Users.builder()
                .name("정상희")
                .email("wjdtkdgml7352@naver.com")
                .password(encoder.encrypt("11111")) // user정보를 저장하는 시점에 암호화
                .build());
        Login login = Login.builder()
                .email("wjdtkdgml7352@naver.com")
                .password("25345")
                .build();

        // expected -> 행동을 하는 순간 오류가 발생한다면
        assertThrows(InvalidLoginInformation.class,
                () -> authService.signIn(login));
    }

}