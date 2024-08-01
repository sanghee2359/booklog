//package com.api.booklog.annotation;
//
//import com.api.booklog.repository.UserRepository;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.test.context.support.WithSecurityContextFactory;
//
//public class MockUserFactory implements WithSecurityContextFactory<CustomWithMockUser> {
//    private UserRepository userRepository;
//    // test가 돌기 전 미리 사용자 컨텍스트를 만들어놓는 기능
//    @Override
//    public SecurityContext createSecurityContext(CustomWithMockUser annotation) {
//        int level = annotation.level();
//        String username = annotation.username();
//        return new SecurityContext() {
//            @Override
//            public Authentication getAuthentication() {
//                return null;
//            }
//
//            @Override
//            public void setAuthentication(Authentication authentication) {
//
//            }
//        };
//    }
//}
