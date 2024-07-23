package com.api.booklog.service;

import com.api.booklog.domain.Users;
import com.api.booklog.exception.AlreadyExistEmail;
import com.api.booklog.exception.InvalidLoginInformation;
import com.api.booklog.repository.UserRepository;
import com.api.booklog.request.auth.Login;
import com.api.booklog.request.auth.SignUp;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

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
    }

    @Test
    @DisplayName("이미 존재하는 이메일은 중복 가입이 불가하다")

    void signup_email_fail() {
        // given
        Users user = userRepository.save(Users.builder()
                .name("user1")
                .email("user1@naver.com")
                .password("user1")
                .build());

        SignUp signUp = SignUp.builder()
                .name(user.getName())
                .password(user.getPassword())
                .email(user.getEmail()).
                build();

        // expected
        assertThrows(AlreadyExistEmail.class, ()-> authService.signUp(signUp));
    }

    @Test
    @DisplayName("로그인 성공 - cryptography")
    void login_success() {
        // given
        SignUp signUp = SignUp.builder()
                .email("wjdtkdgml7352@naver.com")
                .password("1234")
                .name("정상희")
                .build();
        authService.signUp(signUp);

        Login login = Login.builder()
                .email("wjdtkdgml7352@naver.com")
                .password("1234")
                .build();

        // when
        Long userId = authService.signIn(login);

        // then
        assertNotNull(userId);
    }


    @Test
    @DisplayName("로그인 실패 - cryptography")
    void login_failed() {
        // given
        SignUp signUp = SignUp.builder()
                .email("wjdtkdgml7352@naver.com")
                .password("1234")
                .name("정상희")
                .build();
        authService.signUp(signUp);

        Login login = Login.builder()
                .email("wjdtkdgml7352@naver.com")
                .password("25345")
                .build();

        // expected -> 행동을 하는 순간 오류가 발생한다면
        assertThrows(InvalidLoginInformation.class,
                () -> authService.signIn(login));
    }

}