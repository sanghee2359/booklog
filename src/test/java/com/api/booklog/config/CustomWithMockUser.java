package com.api.booklog.config;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = MockSecurityContext.class)
public @interface CustomWithMockUser {
    String name() default "정상희";
    String email() default "wjdtkdgml7352@naver.com";
    String password() default "sanghee065";
//    String role() default "ROLE_ADMIN";
}
