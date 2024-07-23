package com.api.booklog.controller;

import com.api.booklog.request.post.PostCreate;
import com.api.booklog.request.post.PostEdit;
import com.api.booklog.request.post.PostSearch;
import com.api.booklog.response.PostResponse;
import com.api.booklog.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping("/posts")
    public void post(@RequestBody @Valid PostCreate request) throws Exception {
        request.validate();
        postService.write(request);
    }
    // 조회 API
    @GetMapping("/posts/{postId}")
    public PostResponse get(@PathVariable Long postId) {
        return postService.get(postId);
    }

    @GetMapping("/posts")
    public List<PostResponse> getList(@ModelAttribute PostSearch postSearch) {
        return postService.getList(postSearch);
    }

    // 수정 API
    @PatchMapping("/posts/{postId}")
    public void edit(@PathVariable Long postId
            , @RequestBody @Valid PostEdit request, @RequestHeader String authorization) {
        if(authorization.equals("author")) {
            postService.edit(postId, request);
        }
    }

    @DeleteMapping("/posts/{postId}")
    public void delete(@PathVariable Long postId, @RequestHeader String authorization) {
        if(authorization.equals("author")) {
            postService.delete(postId);
        }
    }
}

