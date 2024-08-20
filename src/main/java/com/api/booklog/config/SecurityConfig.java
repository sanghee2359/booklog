package com.api.booklog.config;

import com.api.booklog.config.filter.EmailPasswordAuthFilter;
import com.api.booklog.config.handler.Http401Handler;
import com.api.booklog.config.handler.Http403Handler;
import com.api.booklog.config.handler.LoginFailHandler;
import com.api.booklog.config.handler.LoginSuccessHandler;
import com.api.booklog.domain.Users;
import com.api.booklog.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.session.security.web.authentication.SpringSessionRememberMeServices;

@Configuration
@EnableWebSecurity // 배포환경에서만 사용 (debug = true)
@RequiredArgsConstructor
public class SecurityConfig {
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                .requestMatchers("/favicon.ico")
                .requestMatchers("/error");
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests((auth)-> auth
                        .anyRequest().permitAll())

                // UsernamePasswordAuthenticationFilter 뒤에 동작하도록 체인 걸기
                .addFilterBefore(userNamePasswordAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .rememberMe(rm -> rm.rememberMeParameter("remember")
                        .alwaysRemember(false)
                        .tokenValiditySeconds(2592000)) // 30일 자동로그인 유지
//                .userDetailsService(userDetailsService())
                .exceptionHandling(e -> {
                    e.accessDeniedHandler(new Http403Handler(objectMapper));
                    e.authenticationEntryPoint(new Http401Handler(objectMapper));
                })
                .csrf(AbstractHttpConfigurer::disable)
                .build();
    }

    @Bean
    public EmailPasswordAuthFilter userNamePasswordAuthenticationFilter() {
        EmailPasswordAuthFilter filter = new EmailPasswordAuthFilter("/auth/login", objectMapper);
        filter.setAuthenticationManager(authenticationManager());
        filter.setAuthenticationSuccessHandler(new LoginSuccessHandler());
        filter.setAuthenticationFailureHandler(new LoginFailHandler(objectMapper));
        // 인증 완료 후, 요청 내에서 인증이 유효하기 위해 반드시 필요 -> 세션 발급하기
        filter.setSecurityContextRepository(new HttpSessionSecurityContextRepository());

        // rememberMe setting
        SpringSessionRememberMeServices rememberMeServices = new SpringSessionRememberMeServices();
        rememberMeServices.setAlwaysRemember(true);
        rememberMeServices.setValiditySeconds(3600 * 24 * 30); // 한 달
        filter.setRememberMeServices(rememberMeServices);

        return filter;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService(userRepository));
        provider.setPasswordEncoder(passwordEncoder());

        return new ProviderManager(provider);
    }


    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> {
                Users user = userRepository.findByEmail(username)
                        .orElseThrow(() -> new UsernameNotFoundException(username + "을/를 찾을 수 없습니다."));
                return new UserPrincipal(user);
        };
    }
    @Bean
    public PasswordEncoder passwordEncoder() { // 비밀번호를 암호화
        return new SCryptPasswordEncoder(
                16,
                8,
                1,
                32,
                64);

    }

}
