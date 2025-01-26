package com.api.booklog.service;

import com.api.booklog.domain.Post;
import com.api.booklog.domain.UserEntity;
import com.api.booklog.exception.BookmarkNotFound;
import com.api.booklog.exception.PostNotFound;
import com.api.booklog.exception.UserNotFound;
import com.api.booklog.repository.UsersRepository;
import com.api.booklog.repository.post.PostRepository;
import com.api.booklog.response.BookmarkResponse;
import com.api.booklog.response.PagingResponse;
import com.api.booklog.response.PostResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
@Slf4j
@Service
@RequiredArgsConstructor
public class BookMarkService {
    private final RedisTemplate<String, String> redisTemplate;
    private final PostRepository postRepository;
    private final UsersRepository userRepository;
    private static final String BOOKMARK_KEY_PREFIX = "bookmark:";

    public void addBookmark(String email, Long postId) {
        UserEntity user = findUserByEmail(email);
        postRepository.findById(postId).orElseThrow(PostNotFound::new);

        // 이미 북마크에 존재하는지 확인
        String key = makeKey(user.getId());

        // current time을 score로 사용하여 추가
        redisTemplate.opsForZSet().add(key, postId.toString(), System.currentTimeMillis());
        log.debug("Bookmark added: userId={}, postId={}, key={}", user.getId(), postId, key); // 로그 추가

    }

    public PagingResponse<PostResponse> getBookmarks(String email, int page, int size) {

        UserEntity user = findUserByEmail(email);
        String key = makeKey(user.getId());
        long totalElements = 0;
        // 전체 북마크 수
        if(redisTemplate.opsForZSet().size(key)!=null) {
            totalElements = redisTemplate.opsForZSet().size(key);
        }
        long start = (long)(page - 1) * size;
        long end = start + size - 1;

        // 특정 범위의 데이터 가져오기 (ZSet에서 범위 가져오기)
        Set<ZSetOperations.TypedTuple<String>> bookmarkTuples = redisTemplate.opsForZSet().reverseRangeWithScores(key, start, end);

        // 북마크 ID 리스트 생성
        assert bookmarkTuples != null;
        List<Long> bookmarkIds = bookmarkTuples.stream()
                .map(tuple -> Long.valueOf(Objects.requireNonNull(tuple.getValue())))
                .toList();

        // 데이터베이스에서 정렬된 포스트 조회
        List<Post> posts =  postRepository.findPostsByIdsOrderByIdCustom(bookmarkIds);

        Page<Post> postPage = new PageImpl<>(posts,
                PageRequest.of(page - 1, size),
                totalElements);

        // PagingResponse 생성
        PagingResponse<PostResponse> pagingResponse = new PagingResponse<>(postPage, PostResponse.class);
        pagingResponse.setHasNextPage(totalElements > (start + size));

        return pagingResponse;
    }

    public void removeBookmark(Long userId, Long postId) {
        postRepository.findById(postId).orElseThrow(PostNotFound::new);

        String key = makeKey(userId);
        if(!isExistInZSet(key, postId.toString())) {
            throw new BookmarkNotFound();
        }
        redisTemplate.opsForZSet().remove(key, postId.toString());
        log.debug("Bookmark remove: userId={}, postId={}, key={}", userId, postId, key); // 로그 추가
    }

    public void removeBookmarkByKey(Long userId) {
        // Redis에서 해당 키가 존재하는지 확인 후 삭제
        redisTemplate.delete(makeKey(userId));
    }

    public String makeKey(Long userId) {
        return BOOKMARK_KEY_PREFIX + userId;
    }


    public boolean isExistInZSet(String key, String value) {
        Double score = redisTemplate.opsForZSet().score(key, value);
        return score != null;
    }

    public boolean isExistsKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public BookmarkResponse toggleBookmark(String email, Long postId) {
        UserEntity user = findUserByEmail(email);
        postRepository.findById(postId).orElseThrow(PostNotFound::new);

        boolean isBookmarked = isExistInZSet(makeKey(user.getId()), postId.toString());
        boolean newStatus;

        if (isBookmarked) {
            removeBookmark(user.getId(), postId);
            newStatus = false;
        }
        else {
            addBookmark(user.getEmail(), postId);
            newStatus = true;
        }
        return new BookmarkResponse(postId, newStatus);
    }

    public boolean checkBookmarkStatus(String email, Long postId) {
        UserEntity user = findUserByEmail(email);
        postRepository.findById(postId).orElseThrow(PostNotFound::new);

        return isExistInZSet(makeKey(user.getId()), postId.toString());
    }

    private UserEntity findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(UserNotFound::new);
    }
}
