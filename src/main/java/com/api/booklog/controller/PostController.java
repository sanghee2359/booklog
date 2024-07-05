package com.api.booklog.controller;

import com.api.booklog.request.PostCreate;
import com.api.booklog.request.PostEdit;
import com.api.booklog.request.PostSearch;
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
    @GetMapping("/gets")
    public String get() {
        return "HEllo world";
    }
    @PostMapping("/posts") // post 요청엔 return 값 x
    public void post(@RequestBody @Valid PostCreate request) throws Exception {
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
            , @RequestBody @Valid PostEdit request) {
        postService.edit(postId, request);
    }
}

