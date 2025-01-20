package com.api.booklog.service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.api.booklog.domain.UserEntity;
import com.api.booklog.exception.AlreadyExistUserInformation;
import com.api.booklog.exception.InvalidRefreshToken;
import com.api.booklog.exception.UserNotFound;
import com.api.booklog.repository.UsersRepository;
import com.api.booklog.request.auth.SignUpReq;
import com.api.booklog.response.auth.SignedInUser;
import com.api.booklog.security.Constants;
import com.api.booklog.security.JwtManager;
import com.api.booklog.security.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.hibernate.query.sqm.tree.SqmNode.log;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{
    private final UsersRepository repository;
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
        Optional<UserEntity> objectUser = repository.findByEmail(userEmail);
        return objectUser.orElseThrow(UserNotFound::new);
    }

    @Override
    public Optional<SignedInUser> createUser(SignUpReq request) {
        Integer count = repository.findByNameOrEmail(request.getName(), request.getEmail());
        if(count > 0) {
            throw new AlreadyExistUserInformation();
        }
        LOG.info(request.getPassword());
        UserEntity user = repository.save(toEntity(request));
        return Optional.of(createSignedUserWithRefreshToken(user));
    }
    private SignedInUser createSignedUserWithRefreshToken(UserEntity user) {
        return createSignedInUser(user)
                .refreshToken(createRefreshToken(user));
    }

    private SignedInUser createSignedInUser(UserEntity user) {
        // accessToken 생성로직
        String token = tokenManager.create(
                org.springframework.security.core.userdetails.User.builder()
                .username(user.getName()) // 사용자 이름
                .password(user.getPassword()) // 암호화된 비밀번호
                .authorities(Objects.nonNull(user.getRole()) ? user.getRole().name() : "ROLE_USER") // 권한
                .build());
        return new SignedInUser().name(user.getName()).accessToken(token)
                .userId(user.getId());
    }

    private String createRefreshToken(UserEntity user) {
        // Redis에 refreshToken 저장
        String refreshToken = RandomHolder.randomKey(128); // 고유한 refreshToken 생성
        // userId -> refreshToken 저장
        redisTemplate.opsForValue().set(Constants.USER_REFRESH_TOKEN + user.getId()
                , refreshToken, 7, TimeUnit.DAYS);

        // refreshToken -> userId 저장
        redisTemplate.opsForValue().set(Constants.REFRESH_TOKEN_PREFIX + refreshToken
                , user.getId().toString(), 7, TimeUnit.DAYS);
        return refreshToken; // Redis에 저장된 refreshToken 반환
    }

    @Override
    @Transactional
    public SignedInUser getSignedInUser(UserEntity user) {
        // Redis에서 기존 refreshToken 가져오기
        String existingRefreshToken = redisTemplate.opsForValue().get(Constants.USER_REFRESH_TOKEN + user.getId());
        // 기존 데이터 삭제 (existingRefreshToken이 있을 경우만)
        if (existingRefreshToken != null) {
            deleteTokensFromRedis(user.getId().toString(), existingRefreshToken);
        }
        return createSignedUserWithRefreshToken(user);
    }

    private void deleteTokensFromRedis(String userId, String refreshToken) {
        redisTemplate.delete(Constants.REFRESH_TOKEN_PREFIX + refreshToken);
        redisTemplate.delete(Constants.USER_REFRESH_TOKEN + userId);
    }
    // AccessToken을 얻는 메소드
    public Optional<SignedInUser> getAccessToken(RefreshToken token) {
        // Redis에서 해당 userId에 해당하는 refreshToken을 가져옴
        String storedTokenValue = redisTemplate.opsForValue().get(Constants.REFRESH_TOKEN_PREFIX + token.refreshToken);
        if (storedTokenValue == null) {
            throw new InvalidRefreshToken();
        }
        // 사용자 조회
        UserEntity userEntity = repository.findById(Long.valueOf(storedTokenValue))
                .orElseThrow(UserNotFound::new);

        // SignedInUser 생성 및 Refresh Token 추가 설정

        return Optional.of(createSignedInUser(userEntity)
                .refreshToken(token.getRefreshToken()));
    }
    @Override
    public void removeRefreshToken(RefreshToken token) {
        String key = Constants.REFRESH_TOKEN_PREFIX + token.getRefreshToken();
        // Redis에서 refreshToken -> userId 가져오기
        String userId = redisTemplate.opsForValue().get(key);
        if (userId == null) {
            throw new InvalidRefreshToken();
        }
        // Redis에서 관련 데이터 삭제
        deleteTokensFromRedis(userId, token.getRefreshToken());
    }

    private UserEntity toEntity(SignUpReq request) {
        // 비밀번호가 null일 경우 처리
        if (request.getPassword() == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        UserEntity user = new UserEntity();
        BeanUtils.copyProperties(request, user);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
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
