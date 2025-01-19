package com.api.booklog.service;

import com.api.booklog.domain.Comment;
import com.api.booklog.domain.Post;
import com.api.booklog.domain.UserEntity;
import com.api.booklog.exception.CommentNotFound;
import com.api.booklog.exception.InvalidPassword;
import com.api.booklog.exception.PostNotFound;
import com.api.booklog.exception.UserNotFound;
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
    private final UsersRepository usersRepository;
    private final CommentRepository commentRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);

    @Transactional
    public void write(Long postId, CommentCreate request) {
        try {
            Post post = postRepository.findById(postId)
                    .orElseThrow(PostNotFound::new);

            UserEntity user = getUserIfExists(request.getUserId());
            Comment comment = createComment(user, post, request);

            commentRepository.save(comment);

            if (user != null) {
                user.addComment(comment);
                post.addComment(comment);
            }
        } catch (Exception e) {
            log.error("Error occurred while writing comment: {}", e.getMessage(), e);
            throw e; // 원래 예외를 던져서 호출한 쪽에서 처리할 수 있도록
        }
    }
    private UserEntity getUserIfExists(Long userId) {
        return userId != null ? usersRepository.findById(userId)
                .orElseThrow(UserNotFound::new) : null;
    }
    private Comment createComment(UserEntity user, Post post, CommentCreate request) {
        if (user != null) {
            return new Comment(user, post, user.getPassword(), request.getContent(), user.getName());
        } else {
            String encryptedPassword = passwordEncoder.encode(request.getPassword());
            return new Comment(user, post, encryptedPassword, request.getContent(), request.getAuthor());
        }
    }

    public void delete(Long commentId, CommentDelete request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFound::new);
        String encryptedPassword = comment.getPassword();
        if(!passwordEncoder.matches(encryptedPassword, request.getPassword())) {
            throw new InvalidPassword();
        }
        commentRepository.delete(comment);
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

