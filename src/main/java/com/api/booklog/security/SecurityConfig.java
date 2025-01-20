package com.api.booklog.security;

import com.api.booklog.config.UserPrincipal;
import com.api.booklog.domain.UserEntity;
import com.api.booklog.repository.UsersRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.List;

import static com.api.booklog.security.Constants.*;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    @Value("${app.security.jwt.keystore-location}")
    private String keyStoreLocation;
    @Value("${app.security.jwt.keystore-password}")
    private String keyStorePassword;
    @Value("${app.security.jwt.key-alias}")
    private String keyAlias;
    @Value("${app.security.jwt.private-key-passphrase}")
    private String privateKeyPassphrase;

    private final Logger LOG = LoggerFactory.getLogger(getClass());
    private final UserDetailsService userService;
    private final PasswordEncoder bCryptPasswordEncoder;

    private final ObjectMapper mapper;

    public SecurityConfig(
            @Lazy UserDetailsService userService,
            @Lazy PasswordEncoder bCryptPasswordEncoder,
            @Lazy ObjectMapper mapper) {
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.mapper = mapper;
    }
    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(req -> req
                        .requestMatchers(new AntPathRequestMatcher(TOKEN_URL, HttpMethod.POST.name())).permitAll()
                        .requestMatchers(new AntPathRequestMatcher(TOKEN_URL, HttpMethod.DELETE.name())).permitAll()
                        .requestMatchers(new AntPathRequestMatcher(SIGNUP_URL, HttpMethod.POST.name())).permitAll()
                        .requestMatchers(new AntPathRequestMatcher(REFRESH_URL, HttpMethod.POST.name())).permitAll()
                        .requestMatchers(new AntPathRequestMatcher(POST_URL, HttpMethod.GET.name())).permitAll()
                        .requestMatchers("/api/v1/addresses/**").hasAuthority(Role.ADMIN.getAuthority())
                        .anyRequest().authenticated())
                .csrf(csrf-> csrf.ignoringRequestMatchers(API_URL_PREFIX))
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 활성화
                .oauth2ResourceServer(oauth2ResourceServer ->
                        oauth2ResourceServer.jwt(jwt -> jwt.jwtAuthenticationConverter(getJwtAuthenticationConverter())));

        // Jwt 기반 stateless -> 부모-자식 스레드 간 SecurityContext 공유
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);

        return http.build();
    }

    @Bean // keyStore을 위한 새 빈
    public KeyStore keyStore() {
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            InputStream resStream = Thread.currentThread()
                    .getContextClassLoader().getResourceAsStream(keyStoreLocation);
            keyStore.load(resStream, keyStorePassword.toCharArray());
            LOG.info("Keystore loaded successfully from location: {}", keyStoreLocation);
            return keyStore;
        } catch (IOException | CertificateException | NoSuchAlgorithmException | KeyStoreException e) {
            LOG.error("Unable to load keystore: {}", keyStoreLocation, e);
        }
        throw new IllegalArgumentException("Can't load keystore");
    }


    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "PUT", "POST", "DELETE", "PATCH"));
        // configuration.setAllowCredentials(true);
        // For CORS response headers
        configuration.addAllowedOrigin("*"); // 허용할 도메인
        configuration.addAllowedHeader("*"); // 모든 헤더 허용
        configuration.addAllowedMethod("*"); // 모든 HTTP 메서드 허용
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 URL에 대해 위 설정 적용
        return source;
    }
    private Converter<Jwt, AbstractAuthenticationToken> getJwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authorityConverter = new JwtGrantedAuthoritiesConverter();
        authorityConverter.setAuthorityPrefix(AUTHORITY_PREFIX);
        authorityConverter.setAuthoritiesClaimName(ROLE_CLAIM);
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authorityConverter);
        return converter;
    }
    @Bean
    public UserDetailsService userDetailsService(UsersRepository userRepository) {
        return username -> {
            UserEntity user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException(username + "을/를 찾을 수 없습니다."));
            return new UserPrincipal(user);
        };
    }

    @Bean
    public RSAPrivateKey jwtSigningKey (KeyStore keyStore) {
        try {
            Key key = keyStore.getKey(keyAlias, privateKeyPassphrase.toCharArray());
            if(key instanceof RSAPrivateKey) {
                return (RSAPrivateKey) key;
            }
            LOG.error("Key is not an RSAPrivateKey: {}", key.getClass());

        } catch (UnrecoverableKeyException | NoSuchAlgorithmException | KeyStoreException e) {
            LOG.error("Unable to load private key from keystore: {}", keyStoreLocation, e);
        }
        throw new IllegalArgumentException("Unable to load private key");
    }

    @Bean
    public RSAPublicKey jwtValidationKey (KeyStore keyStore) {
        try {
            Certificate certificate = keyStore.getCertificate(keyAlias);
            PublicKey publicKey = certificate.getPublicKey();
            if(publicKey instanceof RSAPublicKey) {
                return (RSAPublicKey) publicKey;
            }
            LOG.error("PublicKey is not an RSAPublicKey: {}", publicKey.getClass());
        } catch (KeyStoreException e) {
            LOG.error("Unable to load private key from keystore: {}", keyStoreLocation, e);
        }
        throw new IllegalArgumentException("Unable to load RSA public key");
    }

    @Bean // jwt decoder인터페이스 구현
    public JwtDecoder jwtDecoder(RSAPublicKey rsaPublicKey) {
        return NimbusJwtDecoder.withPublicKey(rsaPublicKey).build();
    }
}
