package com.api.booklog.controller;

import com.api.booklog.exception.Unauthorized;
import com.api.booklog.request.post.PostCreate;
import com.api.booklog.request.post.PostEdit;
import com.api.booklog.request.post.PostSearch;
import com.api.booklog.response.PagingResponse;
import com.api.booklog.response.PostResponse;
import com.api.booklog.response.UserResponse;
import com.api.booklog.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping("/posts")
    public void post(Authentication authentication , @RequestBody @Valid PostCreate request) throws Exception {
        if(authentication == null) throw new Unauthorized();
        request.validate();
        postService.write(authentication.getName(), request);
    }

    // 조회 API
    @GetMapping("/posts/{postId}")
    public PostResponse get(@PathVariable(name = "postId") Long postId) {
        return postService.get(postId);
    }

    @GetMapping("/posts/{postId}/getuser")
    public UserResponse getUser(@PathVariable(name = "postId") Long postId) {
        return postService.getUser(postId);
    }


    @GetMapping("/posts")
    public PagingResponse<PostResponse> getList(@ModelAttribute PostSearch postSearch) {
        return postService.getList(postSearch);
    }
    
//    @PreAuthorize("isAuthenticated()")
    @GetMapping("/posts/myPage")
    public PagingResponse<PostResponse> getListByUser(
            Authentication authentication , @RequestParam int page, @RequestParam int size) {
        if(authentication == null) throw new Unauthorized();
        PostSearch postSearch = new PostSearch(page, size);
        return postService.getListByUser(authentication.getName(), postSearch);
    }
    // 수정 API
    @PatchMapping("/posts/{postId}")
    public void edit(
            Authentication authentication ,
            @PathVariable Long postId,
            @RequestBody @Valid PostEdit request) {
        if(authentication == null) throw new Unauthorized();
        postService.edit(authentication.getName(), postId, request);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @DeleteMapping("/posts/{postId}")
    public void delete(Authentication authentication , @PathVariable Long postId) {
        if(authentication == null) throw new Unauthorized();
        postService.delete(authentication.getName(), postId);
    }
}