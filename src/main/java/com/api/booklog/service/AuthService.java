package com.api.booklog.service;

import com.api.booklog.domain.UserEntity;
import com.api.booklog.request.auth.SignUpReq;
import com.api.booklog.response.auth.SignedInUser;
import com.api.booklog.security.RefreshToken;

import java.util.Optional;

public interface AuthService {
    UserEntity findUserByEmail(String email);

    Optional<SignedInUser> createUser(SignUpReq request);

    SignedInUser getSignedInUser(UserEntity user);
    Optional<SignedInUser> getAccessToken(RefreshToken refreshToken);
    void removeRefreshToken(RefreshToken refreshToken);
}
