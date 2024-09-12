package com.api.booklog.controller;

import com.api.booklog.config.CustomWithMockUser;
import com.api.booklog.domain.Post;
import com.api.booklog.domain.Users;
import com.api.booklog.repository.post.PostRepository;
import com.api.booklog.repository.UsersRepository;
import com.api.booklog.request.post.PostCreate;
import com.api.booklog.request.post.PostEdit;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;
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

    @Autowired
    private UsersRepository userRepository;
    @AfterEach
    void clean() {
        postRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @CustomWithMockUser
    @DisplayName("글 작성 요청 시 title 값은 필수다.")
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
    @CustomWithMockUser
    @DisplayName("글 작성")
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
        Users user = Users.builder()
                .name("정상희")
                .email("wjdtkdgml7352.naver.com")
                .password("sanghee065")
                .build();
        userRepository.save(user);
        // given
        Post post = Post.builder()
                .title("title")
                .content("content")
                .user(user)
                .build();
        postRepository.save(post);


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
        Users user = Users.builder()
                .name("정상희")
                .email("wjdtkdgml7352.naver.com")
                .password("sanghee065")
                .build();
        userRepository.save(user);

        List<Post> requestPosts = IntStream.range(0, 10)
                .mapToObj(i -> Post.builder()
                        .title("제목 - "+ i)
                        .content("데이터" + i)
                        .user(user)
                        .build())
                .toList();
        postRepository.saveAll(requestPosts);


        // expected
        mockMvc.perform(get("/posts?page=1&sort=id,desc&size=10") // 10개 limit을 페이징 하며, 1페이지 가져오기.
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(10)))
                .andExpect(jsonPath("$[0].title").value("제목 - 9"))
                .andExpect(jsonPath("$[0].content").value("데이터9"))

                .andDo(print());
    }
    @Test
    @CustomWithMockUser
    @DisplayName("글 제목 수정")
    void editTitle() throws Exception {
        // given
        // MockUser에서 만든 user을 꺼내온다 -> 왜 7/16일 user이 userRepository에 남은거지?
        Users user = userRepository.findAll().get(1);

        Post post = Post.builder()
                .title("제목 1")
                .content("데이터 1")
                .user(user)
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
    @CustomWithMockUser
    @DisplayName("글 삭제")
    void deleteTest() throws Exception {
        // given
        Users user = userRepository.findAll().get(0);

        Post post = Post.builder()
                .title("제목 1")
                .content("데이터 1")
                .user(user)
                .build();
        postRepository.save(post);

        // expected
        mockMvc.perform(delete("/posts/{postId}", post.getId())
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    // 예외 처리
    // 실패 케이스 작성
    @Test
    @DisplayName("존재하지 않는 글 조회")
    void find_fail() throws Exception {
        // expected
        mockMvc.perform(get("/posts/{postId}", 1L)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @CustomWithMockUser
    @DisplayName("존재하지 않는 글 수정")
    void edit_fail() throws Exception {
        // given
        PostEdit postEdit = PostEdit.builder()
                .title("제목 수정")
                .content("데이터 1")
                .build(); // 제목 수정
        // expected
        mockMvc.perform(patch("/posts/{postId}", 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postEdit))
                )
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @CustomWithMockUser
    @DisplayName("게시글 작성 시 제목에 '바보'는 포함될 수 없다.")
    void write_fail() throws Exception {
        // given
        PostCreate request = PostCreate.builder()
                .title("나는 바보") // 잘못된 요청
                .content("내용입니다")
                .build();
        String json = objectMapper.writeValueAsString(request);
        // when
        mockMvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isBadRequest())
                .andDo(print());

    }
}