package com.api.booklog.controller;

import com.api.booklog.config.UserPrincipal;
import com.api.booklog.request.comment.CommentCreate;
import com.api.booklog.request.comment.CommentDelete;
import com.api.booklog.request.comment.CommentSearch;
import com.api.booklog.response.CommentResponse;
import com.api.booklog.response.PagingResponse;
import com.api.booklog.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/posts/{postId}/comments")
    public void write(
            @PathVariable Long postId,
            @RequestBody @Valid CommentCreate request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) { // 현재 사용자 정보 가져오기

        Long userId = userPrincipal != null ? userPrincipal.getUserId() : null; // 로그인된 사용자 ID

        // userId가 null일 경우 유동닉만 사용
        CommentCreate commentRequest = new CommentCreate(userId, request.getAuthor(),  request.getPassword(), request.getContent());

        // 서비스 호출
        commentService.write(postId, commentRequest);
    }

    @PostMapping("/comments/{commentId}/delete")
    public void delete(@PathVariable Long commentId ,@RequestBody @Valid CommentDelete request){
        commentService.delete(commentId, request);
    }
    // 조회 API
    @GetMapping("/posts/{postId}/comments")
    public PagingResponse<CommentResponse> getComment(
                                               @RequestParam int page,
                                               @RequestParam int size,
                                               @PathVariable(name = "postId") Long postId) {
        CommentSearch commentSearch = new CommentSearch(page, size);
        return commentService.getListByPost(postId, commentSearch);
    }

}


