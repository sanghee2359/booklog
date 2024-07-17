package com.api.booklog.controller;

import com.api.booklog.domain.Users;
import com.api.booklog.repository.SessionRepository;
import com.api.booklog.repository.UserRepository;
import com.api.booklog.request.Login;
import com.api.booklog.request.PostCreate;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionRepository sessionRepository;
    @BeforeEach
    void clean() {
        userRepository.deleteAll();
    }
    @Test
    @DisplayName("로그인 성공")
    void login_success() throws Exception {
        // given
        userRepository.save(Users.builder()
                .name("user1")
                .email("user1@naver.com")
                .password("user1")
                .build());

        // scrypt, bscrypt 를 통한 암호화도 가능

        Login login = Login.builder()
                .email("user1@naver.com")
                .password("user1")
                .build();

        String json = objectMapper.writeValueAsString(login);

        // expected
        mockMvc.perform(post("/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andDo(print());

    }

//    @Test
//    @DisplayName("로그인 시 이메일 입력은 필수다.")
//    void login_fail() throws Exception {
//        // given
//        Login request = Login.builder()
//                .password("sanghee065")
//                .build();
//        String json = objectMapper.writeValueAsString(request);
//        // expected
//        mockMvc.perform(post("/auth/login")
//                        .contentType(APPLICATION_JSON)
//                        .content(json)
//                )
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.code").value("400"))
//                .andExpect(jsonPath("$.message").value("아이디/비밀번호가 올바르지 않습니다."))
//                .andDo(print());
//    }

    @Test
    @Transactional
    @DisplayName("로그인 성공 후 세션 1개 생성")
    void login_success2() throws Exception {
        // given
        Users user = Users.builder()
                .name("user1")
                .email("user1@naver.com")
                .password("user1")
                .build();
        userRepository.save(user);

        // scrypt, bscrypt 를 통한 암호화도 가능

        Login login = Login.builder()
                .email("user1@naver.com")
                .password("user1")
                .build();

        String json = objectMapper.writeValueAsString(login);

        // when
        mockMvc.perform(post("/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andDo(print());
        Users authorizedUser = userRepository.findById(user.getId())
                .orElseThrow(RuntimeException::new);

        Assertions.assertEquals(authorizedUser.getSessions().size(), 1);
    }
    @Test
    @DisplayName("로그인 성공 후 세션 응답")
    void login_session() throws Exception {
        // given
        Users user = Users.builder()
                .name("user1")
                .email("user1@naver.com")
                .password("user1")
                .build();
        userRepository.save(user);

        // scrypt, bscrypt 를 통한 암호화도 가능

        Login login = Login.builder()
                .email("user1@naver.com")
                .password("user1")
                .build();

        String json = objectMapper.writeValueAsString(login);

        // expected
        mockMvc.perform(post("/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken", Matchers.notNullValue()))
                .andDo(print());

    }

}