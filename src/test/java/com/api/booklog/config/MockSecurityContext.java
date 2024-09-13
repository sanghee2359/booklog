package com.api.booklog.config;

import com.api.booklog.domain.Users;
import com.api.booklog.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.List;

@RequiredArgsConstructor
public class MockSecurityContext implements WithSecurityContextFactory<CustomWithMockUser> {
    private final UsersRepository userRepository;
    // test가 돌기 전 미리 사용자 컨텍스트를 만들어놓는 기능
    @Override
    public SecurityContext createSecurityContext(CustomWithMockUser annotation) {
        var user = Users.builder()
                .email(annotation.email())
                .name(annotation.name())
                .password(annotation.password())
                .build();

        userRepository.save(user);

        UserPrincipal principal = new UserPrincipal(user);

        var role = new SimpleGrantedAuthority("ROLE_ADMIN");
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(principal,
                user.getPassword(),
                List.of(role));

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authenticationToken);

        return  context;
    }
}
