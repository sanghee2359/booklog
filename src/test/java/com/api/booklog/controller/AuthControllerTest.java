package com.api.booklog.controller;

import com.api.booklog.repository.UsersRepository;
import com.api.booklog.request.auth.SignUp;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsersRepository userRepository;

    @BeforeEach
    void clean() {
        userRepository.deleteAll();
    }

//    @Test
//    @DisplayName("회원가입")
//    void signup_success() throws Exception {
//        // given
//        SignUp signUp = SignUp.builder()
//                .name("정상희")
//                .password("1234")
//                .email("wjdtkdgml7352@naver.com").
//                build();
//
//        // expected
//        mockMvc.perform(post("/auth/signup")
//                        .contentType(APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(signUp)))
//                .andExpect(status().isOk())
//                .andDo(print());
//    }

}

