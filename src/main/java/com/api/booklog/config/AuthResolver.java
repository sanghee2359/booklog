package com.api.booklog.config;

import com.api.booklog.config.data.UserSession;
import com.api.booklog.exception.Unauthorized;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;


@Slf4j
@RequiredArgsConstructor
public class AuthResolver implements HandlerMethodArgumentResolver {
    private final AppConfig config;
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(UserSession.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        String jws = webRequest.getHeader("Authorization");
        if(jws == null || jws.equals("")) {
            throw new Unauthorized();
        }
        // 복호화

        try {
            Jws<Claims> claims = Jwts.parser()
                    .setSigningKey(config.getJwtKey())
                    .build()
                    .parseSignedClaims(jws);

            String userId = claims.getBody().getSubject();

            return new UserSession(Long.parseLong(userId));

        } catch (JwtException e) {
            throw new Unauthorized();
        }

        // DB 사용자 확인 작업 -> jwt를 사용하므로 DB를 타지 않을 수 있다e

        // 세션 검증이 끝난 후
    }
}
