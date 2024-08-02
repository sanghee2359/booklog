package com.api.booklog.service;

import com.api.booklog.domain.Post;
import com.api.booklog.domain.PostEditor;
import com.api.booklog.exception.InvalidRequest;
import com.api.booklog.exception.PostNotFound;
import com.api.booklog.exception.UserNotFound;
import com.api.booklog.repository.post.PostRepository;
import com.api.booklog.repository.UserRepository;
import com.api.booklog.request.post.PostCreate;
import com.api.booklog.request.post.PostEdit;
import com.api.booklog.request.post.PostSearch;
import com.api.booklog.response.PostResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
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

    public PostResponse get(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow((PostNotFound::new));
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .build();

    }
    public List<PostResponse> getList(PostSearch postSearch) {
//        Pageable pageable = PageRequest.of(page, 5 , Sort.by(Sort.Direction.DESC,"id"));
        return postRepository.getList(postSearch).stream()
                .map(PostResponse::new)
                .collect(Collectors.toList());

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
//        return new PostResponse(post);
    }

    public void delete(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow((PostNotFound::new));
        postRepository.delete(post);
    }
}
