package com.api.booklog.service;

import com.api.booklog.config.UserPrincipal;
import com.api.booklog.domain.Post;
import com.api.booklog.domain.PostEditor;
import com.api.booklog.exception.InvalidRequest;
import com.api.booklog.exception.PostNotFound;
import com.api.booklog.exception.Unauthorized;
import com.api.booklog.exception.UserNotFound;
import com.api.booklog.repository.post.PostRepository;
import com.api.booklog.repository.UsersRepository;
import com.api.booklog.request.post.PostCreate;
import com.api.booklog.request.post.PostEdit;
import com.api.booklog.request.post.PostSearch;
import com.api.booklog.response.LikeResponse;
import com.api.booklog.response.PagingResponse;
import com.api.booklog.response.PostResponse;
import com.api.booklog.response.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UsersRepository userRepository;
    private final BookMarkService bookMarkService;
    // 게시글 생성

    public void write(Long userId, PostCreate postCreate) {
        // repository.save(postCreate)
        // postCreate 라는 클래스를 entity 형태로 변환

        if(postCreate.getTitle() == null) {
            throw new InvalidRequest("title", "제목을 입력해주세요.");
        }
        var user = userRepository.findById(userId)
                .orElseThrow(UserNotFound::new);

        Post post = Post.builder()
                .user(user)
                .title(postCreate.getTitle())
                .content(postCreate.getContent())
                .build();

        postRepository.save(post);
    }
    // 게시글 조회

    public PostResponse get (Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow((PostNotFound::new));
        return new PostResponse(post);

    }
    // 게시글의 작성자 조회
    public UserResponse getUser(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow((PostNotFound::new));
        return new UserResponse(post.getUserId(), post.getUser().getName());

    }
    public PagingResponse<PostResponse> getList(PostSearch postSearch) {
;
        Page<Post> postPage = postRepository.getList(postSearch);
        PagingResponse<PostResponse> postList = new PagingResponse<>(postPage, PostResponse.class);
        return postList;
    }
    // 게시글 수정
    @Transactional
    public void edit(Long id, PostEdit postEdit) {
        Post post = postRepository.findById(id)
                .orElseThrow((PostNotFound::new));
        PostEditor.PostEditorBuilder postEditorBuilder = post.toEditor();

        PostEditor postEditor = postEditorBuilder
                // 수정이 없어 null 값이 들어온다면,
                // 기존의 db에 저장된 post의 필드값이 저장됨.
                .title(postEdit.getTitle())
                .content(postEdit.getContent())
                .build();
        post.edit(postEditor);
    }

    public void delete(Long userId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow((PostNotFound::new));
        bookMarkService.removeBookmark(userId, postId);
        postRepository.delete(post);
    }


}
