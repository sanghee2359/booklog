package com.api.booklog.response.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class SignedInUser {
    private String accessToken;
    private String refreshToken;
    private String name;
    private Long userId;

    public SignedInUser name(String name) {
        return this.toBuilder()
                .name(name)
                .build();
    }

    public SignedInUser accessToken(String accessToken) {
        return this.toBuilder()
                .accessToken(accessToken)
                .build();
    }
    public SignedInUser refreshToken(String refreshToken) {
        return this.toBuilder()
                .refreshToken(refreshToken)
                .build();
    }

    public SignedInUser userId(Long userId) {
        return this.toBuilder()
                .userId(userId)
                .build();
    }
}
