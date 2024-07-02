package com.api.booklog.controller;

import com.api.booklog.domain.Post;
import com.api.booklog.request.PostCreate;
import com.api.booklog.response.PostResponse;
import com.api.booklog.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;



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
    @GetMapping("/posts/{postId}")
    public PostResponse get(@PathVariable(name = "postId") Long id) {
        PostResponse response = postService.get(id);
        return response;
    }
}
