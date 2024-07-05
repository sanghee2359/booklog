package com.api.booklog.controller;

import com.api.booklog.domain.Post;
import com.api.booklog.repository.PostRepository;
import com.api.booklog.request.PostCreate;
import com.api.booklog.request.PostEdit;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class PostControllerTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostRepository postRepository;
    @BeforeEach
    void clean() {
        postRepository.deleteAll();
    }

    @Test
    @DisplayName("/gets 요청 시 Hello world를 출력함")
    void getTest() throws Exception {
        mockMvc.perform(get("/gets"))
                .andExpect(status().isOk())
                .andExpect(content().string("HEllo world"))
                .andDo(print());
    }
    @Test
    @DisplayName("/posts 요청 시 Hello world를 출력함")
    void postTest() throws Exception {
        // given
        PostCreate request = PostCreate.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();

        String json = objectMapper.writeValueAsString(request);
        System.out.println(json);
        // expected
        mockMvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andExpect(content().string(""))
                .andDo(print());
    }
    @Test
    @DisplayName("/posts 요청 시 title 값은 필수다.")
    void postTitleTest() throws Exception {
        // given
        PostCreate request = PostCreate.builder()
                .content("내용입니다.")
                .build();
        String json = objectMapper.writeValueAsString(request);
        // expected
        mockMvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation.title").value("제목을 입력해주세요."))
                .andDo(print());
    }

    @Test
    @DisplayName("/posts 요청 시 DB에 값이 저장된다.")
    void save() throws Exception {
        // given
        PostCreate request = PostCreate.builder()
                .title("제목입니다.")
                .content("내용입니다")
                .build();
        String json = objectMapper.writeValueAsString(request);
        // when
        mockMvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andDo(print());
        // then
        assertEquals(1L, postRepository.count());
        // DB 저장된 내용 검증
        Post post = postRepository.findAll().get(0);
        assertEquals("제목입니다.",post.getTitle());
        assertEquals("내용입니다",post.getContent());
    }

    @Test
    @DisplayName("글 1개 조회")
    void findById() throws Exception {
        // given
        Post post = Post.builder()
                .title("title")
                .content("content")
                .build();
        postRepository.save(post);

        // 클라이언트 요구사항
        //json응답에서 title 값의 길이를 10글자로 제한해주세요.
        // Post entity <=> PostResponse class

        // expected (when과 then이 mix됨)
        mockMvc.perform(get("/posts/{postId}", post.getId())
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(post.getId()))
                .andExpect(jsonPath("$.title").value("title"))
                .andExpect(jsonPath("$.content").value("content"))
                .andDo(print());
    }
    @Test
    @DisplayName("글 조회 - 페이지를 0으로 요청하여도 첫 페이지를 가져온다.")
    void findAll() throws Exception {
        // given
        List<Post> requestPosts = IntStream.range(0, 10)
                .mapToObj(i -> Post.builder()
                        .title("제목 - "+ i)
                        .content("데이터" + i)
                        .build())
                .toList();
        postRepository.saveAll(requestPosts);


        // expected
        mockMvc.perform(get("/posts?page=1&sort=id,desc&size=10")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(10)))
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].title").value("제목 - 9"))
                .andExpect(jsonPath("$[0].content").value("데이터9"))

                .andDo(print());
    }
    @Test
    @DisplayName("글 제목 수정")
    void editTitle() throws Exception {
        // given
        Post post = Post.builder()
                .title("제목 1")
                .content("데이터 1")
                .build();
        postRepository.save(post);

        PostEdit postEdit = PostEdit.builder()
                .title("제목 수정")
                .content("데이터 1")
                .build(); // 제목 수정

        // expected (when과 then이 mix됨)
        mockMvc.perform(patch("/posts/{postId}", post.getId())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postEdit)) // 수정할 데이터
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("글 삭제")
    void deleteTest() throws Exception {
        // given
        Post post = Post.builder()
                .title("제목 1")
                .content("데이터 1")
                .build();
        postRepository.save(post);

        // expected
        mockMvc.perform(delete("/posts/{postId}", post.getId())
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

    }
}