package com.api.booklog.security.config;

public class Constants {
    public static final String ENCODER_ID = "bcrypt";
    public static final String API_URL_PREFIX = "/v1/**";
    public static final String REFRESH_TOKEN_PREFIX = "refresh_token:";
    public static final String H2_URL_PREFIX = "/h2-console/**";
    public static final String TOKEN_URL = "/v1/auth/token";
    public static final String SIGN_OUT = "/v1/auth/logout";
    public static final String SIGN_UP = "/v1/auth/register";
    public static final String REFRESH_URL = "/v1/auth/token/refresh";
    public static final String POST_URL = "/posts/**";
    public static final String COMMENT_URL = "/posts/{postId}/comments";
    public static final String ANONYMOUS_COMMENT_URL = "/public/posts/{postId}/comments";
    public static final String ANONYMOUS_COMMENT_DEL_URL = "/public/posts/{postId}/comments/{commentId}/delete";
    public static final String AUTHORIZATION = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String SECRET_KEY = "SECRET_KEY";
    public static final long EXPIRATION_TIME = 900_000; // 15 mins
    public static final long REFRESH_TOKEN_TTL_SECONDS = 7 * 24 * 60 * 60; // 7일
    public static final String ROLE_CLAIM = "roles";
    public static final String AUTHORITY_PREFIX = "ROLE_";


}
