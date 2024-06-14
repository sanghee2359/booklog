package com.api.booklog.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@Slf4j
@WebMvcTest
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Test
    @DisplayName("/gets 요청 시 Hello world를 출력함")
    void getTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/gets"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("HEllo world"))
                .andDo(print());
    }
    @Test
    @DisplayName("/posts 요청 시 Hello world를 출력함")
    void postTest() throws Exception {
        // 글 제목
        // 글 내용
        // expected
        mockMvc.perform(MockMvcRequestBuilders.post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"제목입니다\",\"content\":\"내용입니다\"}")
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("{}"))
                .andDo(print());
    }
    @Test
    @DisplayName("/posts 요청 시 title 값은 필수다.")
    void postTitleTest() throws Exception {
        // 글 제목
        // 글 내용
        // expected
        mockMvc.perform(MockMvcRequestBuilders.post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        // {"title" : ""} 일때 에러 발생
                        // {"title" : null} 일때도 에러가 발생할지 확인 -> @NotBlank에서 null도 관리해준다
                        .content("{\"title\":null,\"content\":\"내용입니다\"}")
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.title").value("제목을 입력해주세요."))
                .andDo(print());
    }
}