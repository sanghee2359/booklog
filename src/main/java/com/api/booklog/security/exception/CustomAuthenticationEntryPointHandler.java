package com.api.booklog.security.exception;

import com.api.booklog.response.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.time.Instant;

import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationEntryPointHandler implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;
    private ErrorCode determineErrorCode(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ErrorCode.INVALID_TOKEN; // 잘못된 형식
        }
        return ErrorCode.EXPIRED_TOKEN;  // 토큰 만료
    }
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        log.error("[인증오류] 로그인이 필요합니다.");
        final String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        ErrorCode errorCode = determineErrorCode(authorization);
        ErrorResponse errorResponse = new ErrorResponse(errorCode);

        response.setContentType(APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(UTF_8.name());
        response.setStatus(SC_UNAUTHORIZED);
        objectMapper.writeValue(response.getWriter(), errorResponse);
    }


}