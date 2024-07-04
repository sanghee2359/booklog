package com.api.booklog.service;

import com.api.booklog.domain.Post;
import com.api.booklog.repository.PostRepository;
import com.api.booklog.request.PostCreate;
import com.api.booklog.request.PostSearch;
import com.api.booklog.response.PostResponse;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.data.domain.Sort.Direction.*;

@SpringBootTest
class PostServiceTest {
    @Autowired
    private PostService postService;
    @Autowired
    private PostRepository postRepository;
    @BeforeEach
    void clean() {
        postRepository.deleteAll();
    }

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
    @Test
    @DisplayName("글 1개 조회")
    void findById() {
        // given
        Post requestPost = Post.builder()
                .title("제목입니다")
                .content("내용입니다")
                .build();
        postRepository.save(requestPost);

        // when
        PostResponse response = postService.get(requestPost.getId());

        // then
        assertNotNull(response);
        Post responsePost = postRepository.findAll().get(0);
        assertEquals("제목입니다", responsePost.getTitle());
        assertEquals("내용입니다", responsePost.getContent());
    }

    @Test
    @DisplayName("페이징 - 글 1 페이지 조회")
    void paging() {
        // given
        List<Post> requestPosts = IntStream.range(0, 20)
                .mapToObj(i -> Post.builder()
                            .title("제목 - "+ i)
                            .content("데이터" + i)
                            .build())
                .toList();
        postRepository.saveAll(requestPosts);
        PostSearch postSearch = PostSearch.builder().page(1).size(10).build();

        // when
        List<PostResponse> posts = postService.getList(postSearch);

        // then
        assertEquals(10, posts.size());
        assertEquals("제목 - 19", posts.get(0).getTitle());

    }


}