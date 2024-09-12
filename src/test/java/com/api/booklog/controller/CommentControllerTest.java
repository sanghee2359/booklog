package com.api.booklog.controller;

import com.api.booklog.config.CustomWithMockUser;
import com.api.booklog.domain.Comment;
import com.api.booklog.domain.Post;
import com.api.booklog.domain.Users;
import com.api.booklog.repository.UsersRepository;
import com.api.booklog.repository.post.CommentRepository;
import com.api.booklog.repository.post.PostRepository;
import com.api.booklog.request.comment.CommentCreate;
import com.api.booklog.request.comment.CommentDelete;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class CommentControllerTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UsersRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @AfterEach
    void clean() {
        postRepository.deleteAll();
        commentRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @CustomWithMockUser
    @DisplayName("댓글 작성")
    void commentTset() throws Exception {
        // given
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

        CommentCreate commentCreate = CommentCreate.builder()
                .author("정상준")
                .password("sangjoon")
                .content("우왕 댓글이다~~~~~")
                .build();
        // expected
        String json = objectMapper.writeValueAsString(commentCreate);
        mockMvc.perform(post("/posts/{postId}/comments", post.getId())
                        .contentType(APPLICATION_JSON)
                        .content(json)
                ).andDo(print())
                .andExpect(status().isOk());

    }
    @Test
    @DisplayName("댓글 삭제")
    void deleteTest() throws Exception {
        // given
        Users user = Users.builder()
                .name("정상희")
                .email("wjdtkdgml7352.naver.com")
                .password("sanghee065")
                .build();
        userRepository.save(user);

        Post post = Post.builder()
                .title("title")
                .content("content")
                .user(user)
                .build();
        postRepository.save(post);
        String encryptedPassword = passwordEncoder.encode("123456");

        Comment comment = Comment.builder()
                .author("상희")
                .password("123456")
                .content("댓글답니다 ㅎㅎㅎㅎㅎ")
                .build();

        comment.setPost(post);
        commentRepository.save(comment);

        CommentDelete request = new CommentDelete(encryptedPassword);
        String json = objectMapper.writeValueAsString(request);
        // expected
        mockMvc.perform(post("/comments/{commentId}/delete", comment.getId())
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andDo(print());
    }
}