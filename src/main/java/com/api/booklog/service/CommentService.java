package com.api.booklog.service;

import com.api.booklog.domain.Comment;
import com.api.booklog.domain.Post;
import com.api.booklog.exception.CommentNotFound;
import com.api.booklog.exception.InvalidPassword;
import com.api.booklog.exception.PostNotFound;
import com.api.booklog.repository.post.CommentRepository;
import com.api.booklog.repository.post.PostRepository;
import com.api.booklog.request.comment.CommentCreate;
import com.api.booklog.request.comment.CommentDelete;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void write(Long postId, CommentCreate request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFound::new);


        String encryptedPassword = passwordEncoder.encode(request.getPassword()) ;
        Comment comment = Comment.builder()
                .author(request.getAuthor())
                .password(encryptedPassword)
                .content(request.getContent())
                .build();
        commentRepository.save(comment);

        post.addComment(comment);
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
}

