package com.api.booklog.service;

import com.api.booklog.domain.Comment;
import com.api.booklog.domain.Post;
import com.api.booklog.domain.UserEntity;
import com.api.booklog.exception.*;
import com.api.booklog.repository.UsersRepository;
import com.api.booklog.repository.comment.CommentRepository;
import com.api.booklog.repository.post.PostRepository;
import com.api.booklog.request.comment.CommentCreate;
import com.api.booklog.request.comment.CommentDelete;
import com.api.booklog.request.comment.CommentSearch;
import com.api.booklog.response.CommentResponse;
import com.api.booklog.response.PagingResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentService {
    private final PostRepository postRepository;
    private final UsersRepository userRepository;
    private final CommentRepository commentRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);

    @Transactional
    public void writeAuthenticated(Long postId, String email, CommentCreate request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFound::new);

        UserEntity user = userRepository.findByEmail(email).orElseThrow(UserNotFound::new);
        Comment comment = new Comment(user, post, user.getPassword(), request.getContent(), request.getAuthor());
        commentRepository.save(comment);

        user.addComment(comment);
        post.addComment(comment);
    }
    @Transactional
    public void writeAnonymous(Long postId, CommentCreate request) {
        // userId가 null일 경우 유동닉만 사용
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFound::new);

        String encryptedPassword = passwordEncoder.encode(request.getPassword());
        Comment comment = new Comment(null, post, encryptedPassword, request.getContent(), request.getAuthor());
        commentRepository.save(comment);
        post.addComment(comment);
    }

    @Transactional
    public void delete(Long commentId, Long postId, String email, CommentDelete request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFound::new);
        // validation
        if (!comment.getPost().getId().equals(postId)) {
            throw new IllegalArgumentException("Comment does not belong to the specified post.");
        }
        deleteAsAuthenticatedUser(comment, email, request);
        commentRepository.delete(comment);
    }

    @Transactional
    public void deleteByAnonymous(Long commentId, Long postId, CommentDelete request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFound::new);
        // validation
        if (!comment.getPost().getId().equals(postId)) {
            throw new IllegalArgumentException("Comment does not belong to the specified post.");
        }
        deleteAsGuestUser(comment, request);
        commentRepository.delete(comment);
    }

    // 로그인 유저 비밀번호 비교
    private void deleteAsAuthenticatedUser(Comment comment, String email, CommentDelete request) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(UserNotFound::new);

        if (!isCommentAuthor(comment, user)) {
            throw new Unauthorized();
        }

        verifyPassword(user.getPassword(), request.getPassword());
    }
    // 비로그인 유저 비밀번호 비교
    private void deleteAsGuestUser(Comment comment, CommentDelete request) {
        verifyPassword(comment.getPassword(), request.getPassword());
    }

    private void verifyPassword(String encryptedPassword, String rawPassword) {
        if (!passwordEncoder.matches(rawPassword, encryptedPassword)) {
            throw new InvalidPassword();
        }
    }

    private boolean isCommentAuthor(Comment comment, UserEntity user) {
        return comment.getUser() != null && comment.getUser().getId().equals(user.getId());
    }

    public PagingResponse<CommentResponse> getListByPost(Long postId, CommentSearch commentSearch) {

        // 로깅 - 메서드 시작
        logger.info("Fetching comments for postId: {}, page: {}, size: {}", postId, commentSearch.getPage(), commentSearch.getSize());

        long start = (long)(commentSearch.getPage() - 1) * commentSearch.getSize();

        try {
            // 실제 댓글 조회 로직
            Page<Comment> commentPage = commentRepository.getCommentListByPost(postId, commentSearch);

            PagingResponse<CommentResponse> commentList = new PagingResponse<>(commentPage, CommentResponse.class);
            commentList.setHasNextPage(commentPage.getTotalElements() > (start + commentSearch.getSize()));

            // 로깅 - 댓글 수와 다음 페이지 여부
            logger.info("Fetched {} comments. Has next page: {}", commentPage.getNumberOfElements(), commentList.isHasNextPage());

            return commentList;

        } catch (Exception e) {
            // 에러 발생 시 로그 출력
            logger.error("Error fetching comments for postId: {}", postId, e);
            throw e; // 또는 커스텀 예외 던지기
        }
    }
}

