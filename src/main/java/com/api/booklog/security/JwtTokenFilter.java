package com.api.booklog.security;

import com.api.booklog.security.config.Constants;
import com.api.booklog.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;

@Slf4j
@Component
public class JwtTokenFilter extends OncePerRequestFilter {
    private final JwtDecoder jwtDecoder;
    private final CustomUserDetailsService userDetailsService;
    private final Logger LOG = LoggerFactory.getLogger(getClass());


    // 생성자 주입을 통해 JwtDecoder 주입
    public JwtTokenFilter(JwtDecoder jwtDecoder, CustomUserDetailsService userDetailsService) {
        this.jwtDecoder = jwtDecoder;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = extractToken(request);

        if (token == null) {
            // refreshToken으로 다시 생성
            // refreshAccessToken
        }
        if (token != null) {
            Jwt jwt = jwtDecoder.decode(token);
            if (!isTokenValid(jwt)) {
                filterChain.doFilter(request, response);
            }
            String email = jwt.getSubject();
            // SecurityContextHolder에 인증 객체가 없는 경우 처리
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,          // password가 없으므로 null
                        userDetails.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("Authentication set for user: {}", email);

            }

        }

        filterChain.doFilter(request, response);  // 요청 계속 처리
    }

    // 토큰을 요청 헤더에서 추출
    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith(Constants.TOKEN_PREFIX)) {
            return header.substring(Constants.TOKEN_PREFIX.length());  // "Bearer "를 제외한 토큰 부분
        }
        return null;
    }
    private boolean isTokenValid(Jwt jwt) {
        try {
            Instant expiration = jwt.getExpiresAt();
            return expiration.isAfter(Instant.now());
        } catch (Exception e) {
            log.error("Token validation error", e);
            return false;
        }
    }
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // 특정 경로 제외 (익명 댓글 작성 경로)
        return request.getRequestURI().startsWith("/public/posts/**/comments");
    }

}