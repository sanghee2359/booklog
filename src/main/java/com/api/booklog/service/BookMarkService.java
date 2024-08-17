package com.api.booklog.service;

import com.api.booklog.domain.Post;
import com.api.booklog.exception.AlreadyBookmark;
import com.api.booklog.exception.BookmarkNotFound;
import com.api.booklog.exception.PostNotFound;
import com.api.booklog.repository.post.PostRepository;
import com.api.booklog.response.BookMarkResponse;
import com.api.booklog.response.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookMarkService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final PostService postService;
    private final PostRepository postRepository;
    private static final String BOOKMARK_KEY_PREFIX = "user:bookmarks:";

    public void addBookmark(Long userId, Long postId) {
        postRepository.findById(postId).orElseThrow(PostNotFound::new);

        // 이미 북마크에 존재하는지 확인
        String key = BOOKMARK_KEY_PREFIX + userId;
        if(Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, postId))) {
            throw new AlreadyBookmark();
        }

        redisTemplate.opsForSet().add(key, postId);
    }

    public BookMarkResponse getBookmarks(Long userId) {
        String key = BOOKMARK_KEY_PREFIX + userId;
        // postResponse list
        List<Long> bookmarkIds = Objects.requireNonNull(redisTemplate.opsForSet().members(key)).stream()
                .map(item -> Long.valueOf(item.toString()))
                .toList();

        List<PostResponse> bookmarks = bookmarkIds.stream()
                .map(postService::get)
                .toList();
        return BookMarkResponse.builder()
                .userId(userId)
                .bookmarks(bookmarks)
                .build();
    }

    public void removeBookmark(Long userId, Long postId) {
        postRepository.findById(postId).orElseThrow(PostNotFound::new);

        // 북마크에 존재하지 않는 포스트입니다
        String key = BOOKMARK_KEY_PREFIX + userId;
        if(Boolean.FALSE.equals(redisTemplate.opsForSet().isMember(key, postId))) {
            throw new BookmarkNotFound();
        }
        redisTemplate.opsForSet().remove(key, postId);
    }
}
