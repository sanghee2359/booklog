package com.api.booklog.service;

import com.api.booklog.domain.Post;
import com.api.booklog.repository.PostRepository;
import com.api.booklog.request.PostCreate;
import com.api.booklog.request.PostEdit;
import com.api.booklog.request.PostSearch;
import com.api.booklog.response.PostResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
    @DisplayName("페이징 - 글 첫 번째 페이지 조회")
    void update() {
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

    @Test
    @DisplayName("글 제목 수정")
    void editTitle() {
        // given
        Post post = Post.builder()
                .title("제목 1")
                .content("데이터 1")
                .build();
        postRepository.save(post);

        PostEdit postEdit = PostEdit.builder()
                .title("안녕하세여")
                .content("데이터 1")
                .build(); // 제목 수정
        // when
        postService.edit(post.getId(), postEdit);
        // then
        Post changedPost = postRepository.findById(post.getId())
                .orElseThrow(() -> new RuntimeException("글이 존재하지 않습니다."));
        assertEquals("안녕하세여", changedPost.getTitle());
        assertEquals("데이터 1", changedPost.getContent());
    }

    @Test
    @DisplayName("글 내용 수정")
    void editContent() {
        // given
        Post post = Post.builder()
                .title("제목 1")
                .content("데이터 1")
                .build();
        postRepository.save(post);

        PostEdit postEdit = PostEdit.builder()
                .title("제목 1")
                .content("데이터 수정")
                .build(); // 내용 수정
        // when
        postService.edit(post.getId(), postEdit);
        // then
        Post changedPost = postRepository.findById(post.getId())
                .orElseThrow(() -> new RuntimeException("글이 존재하지 않습니다."));
        assertEquals("제목 1", changedPost.getTitle());
        assertEquals("데이터 수정", changedPost.getContent());
    }

    @Test
    @DisplayName("수정하지 않을 데이터에 null값이 들어올 경우")
    void editNull() {
        // given
        Post post = Post.builder()
                .title("제목 1")
                .content("데이터 1")
                .build();
        postRepository.save(post);

        PostEdit postEdit = PostEdit.builder()
                .title(null) // 수정 안했을 때 null이 들어오는 상황으로 가정
                .content("데이터 수정")
                .build();
        // when
        postService.edit(post.getId(), postEdit);
        // then
        Post changedPost = postRepository.findById(post.getId())
                .orElseThrow(() -> new RuntimeException("글이 존재하지 않습니다."));
        assertEquals("제목 1", changedPost.getTitle());
        assertEquals("데이터 수정", changedPost.getContent());
    }
}