package com.api.booklog.security.config;

import com.api.booklog.security.JwtTokenFilter;
import com.api.booklog.security.Role;
import com.api.booklog.security.exception.CustomAccessDeniedHandler;
import com.api.booklog.security.exception.CustomAuthenticationEntryPointHandler;
import com.api.booklog.service.CustomUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
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
import java.util.*;
import java.util.stream.Collectors;

import static com.api.booklog.security.config.Constants.*;

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
    private final CustomUserDetailsService userService;
    private final ObjectMapper objectMapper;
    private final JwtDecoder jwtDecoder;


    public SecurityConfig(
            @Lazy CustomUserDetailsService userService,
            @Lazy JwtDecoder jwtDecoder,
            @Lazy ObjectMapper objectMapper){
        this.userService = userService;
        this.jwtDecoder = jwtDecoder;
        this.objectMapper = objectMapper;
    }
    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.ignoringRequestMatchers(API_URL_PREFIX, ANONYMOUS_COMMENT_URL, ANONYMOUS_COMMENT_DEL_URL)) // CSRF가 제외된 경로
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(req -> req
                    .requestMatchers(new AntPathRequestMatcher(TOKEN_URL, HttpMethod.POST.name())).permitAll()
                    .requestMatchers(new AntPathRequestMatcher(TOKEN_URL, HttpMethod.DELETE.name())).permitAll()
                    .requestMatchers(new AntPathRequestMatcher(SIGNUP_URL, HttpMethod.POST.name())).permitAll()
                    .requestMatchers(new AntPathRequestMatcher(REFRESH_URL, HttpMethod.POST.name())).permitAll()
                    .requestMatchers(new AntPathRequestMatcher(POST_URL, HttpMethod.GET.name())).permitAll()
                    .requestMatchers(new AntPathRequestMatcher(COMMENT_URL, HttpMethod.GET.name())).permitAll()
                    .requestMatchers(new AntPathRequestMatcher(ANONYMOUS_COMMENT_URL, HttpMethod.POST.name())).permitAll() // 인증에서 제외된 경로
                    .requestMatchers(new AntPathRequestMatcher(ANONYMOUS_COMMENT_DEL_URL, HttpMethod.POST.name())).permitAll()
                    .requestMatchers("/api/v1/addresses/**").hasAuthority(Role.ADMIN.getAuthority())
                    .anyRequest().authenticated())
            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // Stateless 세션 설정
            )
            .addFilterBefore(new JwtTokenFilter(jwtDecoder, userService), UsernamePasswordAuthenticationFilter.class)
//            .addFilterBefore(new CorsLoggingFilter(), JwtTokenFilter.class)
            .exceptionHandling(except -> except
                    .authenticationEntryPoint(new CustomAuthenticationEntryPointHandler(objectMapper))  // 인증 실패
                    .accessDeniedHandler(new CustomAccessDeniedHandler(objectMapper)))  // 권한 부족
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt
                            .jwtAuthenticationConverter(getJwtAuthenticationConverter()))) // JWT 인증 처리기 설정
            .userDetailsService(userService);

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
        configuration.setAllowedOrigins(List.of("http://localhost:5173", "https://book-log.shop"));  // 요청을 허용할 Origin 설정
        configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "PUT", "POST", "DELETE", "PATCH"));
        configuration.setAllowCredentials(true);  // 인증 정보 포함 허용
        configuration.setAllowedHeaders(Arrays.asList(HttpHeaders.CONTENT_TYPE, HttpHeaders.AUTHORIZATION)); // 허용할 헤더 설정
        configuration.setExposedHeaders(Arrays.asList(HttpHeaders.CONTENT_TYPE, HttpHeaders.AUTHORIZATION));  // 노출할 헤더 설정

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);  // 모든 URL에 대해 설정 적용
        return source;
    }
    private Converter<Jwt, AbstractAuthenticationToken> getJwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authorityConverter = new JwtGrantedAuthoritiesConverter();
        authorityConverter.setAuthorityPrefix(""); // 권한을 설정
        authorityConverter.setAuthoritiesClaimName(ROLE_CLAIM);  // "roles" 클레임에서 권한을 추출
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authorityConverter);

        return converter;
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
