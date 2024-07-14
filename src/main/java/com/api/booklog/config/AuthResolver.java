package com.api.booklog.config;

import com.api.booklog.config.data.UserSession;
import com.api.booklog.exception.Unauthorized;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class AuthResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(UserSession.class);
//        return false;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        String accessToken = webRequest.getHeader("Authorization");
        if(accessToken == null || accessToken.equals("")) {
            throw new Unauthorized();
        }
        // DB 사용자 확인 작업
        // ...

        // 지금은 넘어온 값이 없어서 1L 저장
        UserSession userSession = new UserSession(1L);
        userSession.name = accessToken;
        return userSession;
    }
}
