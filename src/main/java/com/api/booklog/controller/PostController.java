package com.api.booklog.controller;

import com.api.booklog.config.UserPrincipal;
import com.api.booklog.request.post.PostCreate;
import com.api.booklog.request.post.PostEdit;
import com.api.booklog.request.post.PostSearch;
import com.api.booklog.response.PagingResponse;
import com.api.booklog.response.PostResponse;
import com.api.booklog.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/posts")
    public void post(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody @Valid PostCreate request) throws Exception {
        request.validate();
        postService.write(userPrincipal.getUserId(),request);
    }
    // 조회 API
    @GetMapping("/posts/{postId}")
    public PostResponse get(@PathVariable(name="postId") Long postId) {
        return postService.get(postId);
    }


    @GetMapping("/posts")
    public PagingResponse<PostResponse> getList(@ModelAttribute PostSearch postSearch) {
        return postService.getList(postSearch);
    }

    // 수정 API
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PreAuthorize("hasRole('ROLE_ADMIN') && hasPermission(#postId, 'POST', 'PATCH')")
    @PatchMapping("/posts/{postId}")
    public void edit(@PathVariable Long postId
            , @RequestBody @Valid PostEdit request) {
        postService.edit(postId, request);
    }


//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PreAuthorize("hasRole('ROLE_ADMIN') && hasPermission(#postId, 'POST', 'DELETE')")
    @DeleteMapping("/posts/{postId}")
    public void delete(@PathVariable Long postId) {
        postService.delete(postId);
    }
}