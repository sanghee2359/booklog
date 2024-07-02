package com.api.booklog.controller;

import com.api.booklog.domain.Post;
import com.api.booklog.request.PostCreate;
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
        // case1. 저장한 데이터 entity -> response로 응답
        // case2. 저장한 데이터의 primary_id -> response로 응답
        //         -> client에서 응답받은 id를 post조회 api에 올려 데이터를 조회함.
        // case3. 응답 필요 없음 -> client에서 모든 post 데이터 context를 잘 관리함
        // bad case : 서버에서 -> 반드시 이렇게 할 것입니다! fix
        //         -> 서버에서 유연하게 대응하는 게 좋다.
        postService.write(request);
    }
}
