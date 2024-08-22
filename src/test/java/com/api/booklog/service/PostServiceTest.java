package com.api.booklog.service;

import com.api.booklog.domain.Post;
import com.api.booklog.domain.Users;
import com.api.booklog.exception.PostNotFound;
import com.api.booklog.repository.post.PostRepository;
import com.api.booklog.repository.UserRepository;
import com.api.booklog.request.post.PostCreate;
import com.api.booklog.request.post.PostEdit;
import com.api.booklog.request.post.PostSearch;
import com.api.booklog.response.PagingResponse;
import com.api.booklog.response.PostResponse;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PostServiceTest {
    @Autowired
    private PostService postService;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;
    @AfterEach
    void clean() {
        postRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("글 작성")
    void write() {
        // given
        var user = Users.builder()
                .name("정상희")
                .email("wjdtkdgml7352.naver.com")
                .password("sanghee065")
                .build();
        userRepository.save(user);

        PostCreate postCreate = PostCreate.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();
        // when
        postService.write(user.getId(), postCreate);

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
    void paging_get() {
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
        PagingResponse<PostResponse> posts = postService.getList(postSearch);

        // then
        assertEquals(10, posts.getSize());

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
                .orElseThrow(PostNotFound::new);
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
                .orElseThrow(PostNotFound::new);
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
                .orElseThrow(PostNotFound::new);
        assertEquals("제목 1", changedPost.getTitle());
        assertEquals("데이터 수정", changedPost.getContent());
    }

    @Test
    @DisplayName("글 삭제")
    void delete() {
        // given
        var user = Users.builder()
                .name("정상희")
                .email("wjdtkdgml7352.naver.com")
                .password("sanghee065")
                .build();
        userRepository.save(user);
        Post post = Post.builder()
                .title("제목 1")
                .content("데이터 1")
                .build();
        postRepository.save(post);

        // when
        postService.delete(user.getId(), post.getId());
        // then
        assertEquals(0, postRepository.count());
    }

    // 예외 처리
    // 실패 케이스 작성
    @Test
    @DisplayName("글 1개 조회")
    void findById_Fail() {
        // given
        Post post = Post.builder()
                .title("제목입니다")
                .content("내용입니다")
                .build();
        postRepository.save(post);

        // expected
        assertThrows(PostNotFound.class, () ->{
            postService.get(post.getId() + 1L);
        });
    }

    @Test
    @DisplayName("글 수정 - 존재 하지 않는 글")
    void editContent_Fail() {
        // given
        Post post = Post.builder()
                .title("제목 1")
                .content("데이터 1")
                .build();
        postRepository.save(post);

        PostEdit postEdit = PostEdit.builder()
                .title("제목 1")
                .content("데이터 수정")
                .build();
        // expected
        assertThrows(PostNotFound.class, () ->{
            postService.edit(post.getId() + 1L, postEdit);

        });
    }


    @Test
    @DisplayName("글 삭제 - 존재 하지 않는 글")
    void delete_Fail() {
        // given
        var user = Users.builder()
                .name("정상희")
                .email("wjdtkdgml7352.naver.com")
                .password("sanghee065")
                .build();
        userRepository.save(user);
        Post post = Post.builder()
                .title("제목 1")
                .content("데이터 1")
                .build();
        postRepository.save(post);

        //expected
        assertThrows(PostNotFound.class, () ->{
            postService.delete(user.getId(), post.getId() + 1L);
        });
    }
}