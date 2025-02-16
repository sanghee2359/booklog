package com.api.booklog.service;

import com.api.booklog.domain.UserEntity;
import com.api.booklog.exception.AlreadyExistUserInformation;
import com.api.booklog.exception.InvalidRefreshToken;
import com.api.booklog.exception.UserNotFound;
import com.api.booklog.repository.UsersRepository;
import com.api.booklog.request.auth.SignUpReq;
import com.api.booklog.response.auth.SignedInUser;
import com.api.booklog.security.JwtManager;
import com.api.booklog.security.RefreshToken;
import com.api.booklog.security.Role;
import com.api.booklog.security.config.Constants;
import io.micrometer.common.lang.Nullable;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{
    private final UsersRepository userRepository;
    private final PasswordEncoder bCryptPasswordEncoder;
    private final JwtManager tokenManager;
    private final RedisTemplate<String, String> redisTemplate;
    private final Logger LOG = LoggerFactory.getLogger(getClass());
    @Override
    public UserEntity findUserByEmail(String email) {
        if(Strings.isBlank(email)) {
            throw new UserNotFound();
        }
        final String userEmail = email.trim();
        Optional<UserEntity> objectUser = userRepository.findByEmail(userEmail);
        return objectUser.orElseThrow(UserNotFound::new);
    }

    @Override
    public Optional<SignedInUser> createUser(SignUpReq request) {
        LOG.info("user:{}",request.toString());
        Long count = userRepository.countByEmail(request.getEmail());
        if(count > 0) {
            throw new AlreadyExistUserInformation();
        }
        UserEntity user = userRepository.save(toEntity(request));
        LOG.info("user:{}",user.toString());

        return Optional.of(createSignedUserWithRefreshToken(user));
    }
    private SignedInUser createSignedUserWithRefreshToken(UserEntity user) {
        return createSignedInUser(user)
                .refreshToken(createRefreshToken(user));
    }
    private String createRefreshToken(UserEntity user) {
        String refreshToken = RandomHolder.randomKey(128); // 고유한 refreshToken 생성
        // refreshToken -> userId 저장
        redisTemplate.opsForValue().set(Constants.REFRESH_TOKEN_PREFIX + refreshToken
                , user.getId().toString(), Constants.REFRESH_TOKEN_TTL_SECONDS, TimeUnit.SECONDS);
        return refreshToken;
    }

    private SignedInUser createSignedInUser(UserEntity user) {
        // accessToken 생성로직
        String token = tokenManager.create(user);
        return new SignedInUser().name(user.getName()).accessToken(token)
                .userId(user.getId());
    }



    @Override
    @Transactional
    public SignedInUser getSignedInUser(UserEntity user, @Nullable RefreshToken token) {
        // 기존 Refresh Token이 존재하는 경우 Redis에서 해당 Token 삭제
        if (token != null) {
            deleteTokensFromRedis(token.refreshToken);
        }
        // 새로운 SignedInUser 반환 (새 Refresh Token 생성 포함)
        return createSignedUserWithRefreshToken(user);
    }

    private void deleteTokensFromRedis(String refreshToken) {
        redisTemplate.delete(Constants.REFRESH_TOKEN_PREFIX + refreshToken);
    }
    // AccessToken을 얻는 메소드
    public Optional<SignedInUser> getAccessToken(RefreshToken token) {
        // Redis에서 해당 refreshtoken 검색
        String userId = redisTemplate.opsForValue().get(Constants.REFRESH_TOKEN_PREFIX + token.refreshToken);
        if (userId == null) throw new InvalidRefreshToken();

        // 사용자 조회
        UserEntity userEntity = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(UserNotFound::new);

        // SignedInUser 생성 및 Refresh Token 추가 설정
        return Optional.of(createSignedInUser(userEntity)
                .refreshToken(token.getRefreshToken()));
    }

    @Override
    public void removeRefreshToken(RefreshToken token) {
        String key = Constants.REFRESH_TOKEN_PREFIX + token.getRefreshToken();
        String userId = redisTemplate.opsForValue().get(key);
        if (userId == null) {
            LOG.warn("Attempt to remove invalid or expired Refresh Token: {}", token.getRefreshToken());
            throw new InvalidRefreshToken();
        }
        // Redis에서 관련 데이터 삭제
        deleteTokensFromRedis(token.getRefreshToken());
    }

    private UserEntity toEntity(SignUpReq request) {
        // 비밀번호가 null일 경우 처리
        if (request.getPassword() == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        UserEntity user = new UserEntity();
        BeanUtils.copyProperties(request, user);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);
        return user;
    }

    public static class RandomHolder {
        static final Random random = new SecureRandom();
        public static String randomKey(int length){
            return String.format("%"+length+"s"
                    , new BigInteger(length*5 /*base 32, 2^5*/, random)
                            .toString(32).replace('\u0020', '0'));
        }
    }
}
