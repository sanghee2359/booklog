package com.api.booklog.service;

import com.api.booklog.domain.Likes;
import com.api.booklog.domain.Post;
import com.api.booklog.domain.UserEntity;
import com.api.booklog.exception.PostNotFound;
import com.api.booklog.exception.UserNotFound;
import com.api.booklog.repository.LikesRepository;
import com.api.booklog.repository.UsersRepository;
import com.api.booklog.repository.post.PostRepository;
import com.api.booklog.response.LikeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikesRepository likeRepository;
    private final PostRepository postRepository;
    private final UsersRepository userRepository;    // 게시글 좋아요 관리
    @Transactional
    public LikeResponse toggleLike(Long postId, String email){
        UserEntity user = findUserByEmail(email);
        Post post = postRepository.findById(postId).orElseThrow(PostNotFound::new);

        // 좋아요가 이미 있는지 확인하고, 있으면 삭제하고 없으면 추가
        likeRepository.findByPostAndUser(post, user)
                .ifPresentOrElse(
                        like -> {
                            // 이미 좋아요가 있으면 삭제 (좋아요 취소)
                            likeRepository.delete(like);
                            post.decrementLikeCount();
                        },
                        () -> {
                            // 좋아요가 없으면 추가
                            likeRepository.save(new Likes(post, user));
                            post.incrementLikeCount();
                        }
                );

        postRepository.save(post);  // Post 엔티티의 변경된 상태를 저장

        // 최종 좋아요 상태 반환 (현재 사용자가 좋아요를 눌렀는지 확인)
        boolean isLiked = likeRepository.existsByPostAndUserId(post, user.getId());
        return new LikeResponse(post.getId(), isLiked, post.getLikesCount());
    }

    public LikeResponse getLikeStatus(Long postId, String email) {
        UserEntity user = findUserByEmail(email);
        Post post = postRepository.findById(postId).orElseThrow(PostNotFound::new);
        if (user == null) {
            // 로그인하지 않은 경우 기본적으로 isLiked를 false
            return new LikeResponse(postId, false, post.getLikesCount());
        }
        boolean isLiked = isLiked(postId, user.getId());
        return new LikeResponse(postId, isLiked, post.getLikesCount());
    }

    // 사용자가 해당 게시물에 좋아요를 눌렀는지 확인하는 메소드
    private boolean isLiked(Long postId, Long userId) {
        Post post = postRepository.findById(postId).orElseThrow(PostNotFound::new);
        return likeRepository.existsByPostAndUserId(post, userId);
    }

    private UserEntity findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(UserNotFound::new);
    }

    public LikeResponse getLikeCount(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(PostNotFound::new);
        return new LikeResponse(post.getId(), false, likeRepository.countByPost(post));
    }
}
