package com.api.booklog.service;

import com.api.booklog.domain.Comment;
import com.api.booklog.domain.Post;
import com.api.booklog.domain.Users;
import com.api.booklog.repository.UserRepository;
import com.api.booklog.repository.post.CommentRepository;
import com.api.booklog.repository.post.PostRepository;
import com.api.booklog.request.comment.CommentCreate;
import com.api.booklog.request.comment.CommentDelete;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CommentServiceTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CommentService commentService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @AfterEach
    void clean() {
        postRepository.deleteAll();
        userRepository.deleteAll();
        commentRepository.deleteAll();
    }
    @Test
    @DisplayName("댓글 작성")
    void write() {
        // given
        var user = Users.builder()
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

        CommentCreate commentCreate = CommentCreate.builder()
                .author("정상준")
                .password("sangjoon")
                .content("우왕 댓글이다~~~~~")
                .build();

        // when
        commentService.write(user.getId(), commentCreate);

        // then
        Assertions.assertEquals(1L,commentRepository.count());
        // DB 저장된 내용 검증
        Comment comment = commentRepository.findAll().get(0);
        assertEquals("정상희.",comment.getAuthor());
        assertNotEquals("sangjoon",comment.getPassword());
        assertEquals("우왕 댓글이다~~~~~",comment.getContent());
        assertTrue(passwordEncoder.matches(comment.getPassword(), "sangjoon"));
    }

    @Test
    @DisplayName("댓글 삭제")
    void delete() throws JsonProcessingException {
        // given
        Users user = Users.builder()
                .name("호돌맨")
                .email("hodolman88@gmail.com")
                .password("1234")
                .build();
        userRepository.save(user);

        Post post = Post.builder()
                .title("123456789012345")
                .content("bar")
                .user(user)
                .build();
        postRepository.save(post);
        String encryptedPassword = passwordEncoder.encode("sanghee065");

        Comment comment = Comment.builder()
                .author("정상희")
                .password("sanghee065")
                .content("제가 한 번 댓글 달아보겠습니다.")
                .build();
        commentRepository.save(comment);

        CommentDelete request = new CommentDelete(encryptedPassword);

        // when
        commentService.delete(comment.getId(), request);

        // then
        assertEquals(0, commentRepository.count());
    }
}
