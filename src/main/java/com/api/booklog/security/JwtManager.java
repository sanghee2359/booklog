package com.api.booklog.security;

import com.api.booklog.domain.UserEntity;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.stream.Collectors;

import static com.api.booklog.security.config.Constants.EXPIRATION_TIME;
import static com.api.booklog.security.config.Constants.ROLE_CLAIM;

@Component
public class JwtManager {
    private final RSAPrivateKey privateKey;
    private final RSAPublicKey publicKey;

    public JwtManager(@Lazy RSAPrivateKey privateKey,
                      @Lazy RSAPublicKey publicKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }
    public String create(UserEntity user) {
        final long now = System.currentTimeMillis();
        return JWT.create()
                .withIssuer("bookLog")
                .withSubject(user.getEmail()) // email이 들어감
                .withClaim(
                        ROLE_CLAIM,
                        user.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList()))
                .withIssuedAt(new Date(now))
                .withExpiresAt(new Date(now + EXPIRATION_TIME))
                .sign(Algorithm.RSA256(publicKey, privateKey));
    }
}
