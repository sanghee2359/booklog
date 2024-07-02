package com.api.booklog.service;

import com.api.booklog.domain.Post;
import com.api.booklog.repository.PostRepository;
import com.api.booklog.request.PostCreate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class PostServiceTest {
    @Autowired
    private PostService postService;
    @Autowired
    private PostRepository postRepository;
    @Test
    @DisplayName("글 작성")
    void write() {
        // given
        PostCreate postCreate = PostCreate.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();
        // when
        postService.write(postCreate);

        // then
        Assertions.assertEquals(1L,postRepository.count());
        // DB 저장된 내용 검증
        Post post = postRepository.findAll().get(0);
        assertEquals("제목입니다.",post.getTitle());
        assertEquals("내용입니다.",post.getContent());

    }

}