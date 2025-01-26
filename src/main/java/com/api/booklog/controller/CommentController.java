package com.api.booklog.controller;

import com.api.booklog.request.comment.CommentCreate;
import com.api.booklog.request.comment.CommentDelete;
import com.api.booklog.request.comment.CommentSearch;
import com.api.booklog.response.CommentResponse;
import com.api.booklog.response.PagingResponse;
import com.api.booklog.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/posts/{postId}/comments")
    public void writeCommentByAuthenticated(
            Authentication authentication,
            @PathVariable Long postId,
            @RequestBody @Valid CommentCreate request) { // 현재 사용자 정보 가져오기

        commentService.writeAuthenticated(postId, authentication.getName(), request);
    }
    @PostMapping("/posts/{postId}/comments/public")
    public void writeCommentByAnonymous(
            @PathVariable Long postId,
            @RequestBody @Valid CommentCreate request) { // 현재 사용자 정보 가져오기
        log.info("여기까지 오나?");
        commentService.writeAnonymous(postId, request);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @PostMapping("/posts/{postId}/comments/{commentId}/delete")
    public void deleteCommentByAuthenticated(
            Authentication authentication,
            @PathVariable Long postId, @PathVariable Long commentId ,
            @RequestBody @Valid CommentDelete request){

        commentService.delete(commentId, postId, authentication.getName(), request);
    }
    @PostMapping("/posts/{postId}/comments/{commentId}/public-delete")
    public void deleteCommentByAnonymous(
            @PathVariable Long postId, @PathVariable Long commentId ,
            @RequestBody @Valid CommentDelete request){
        commentService.deleteByAnonymous(commentId, postId, request);
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


