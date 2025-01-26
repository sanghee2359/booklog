package com.api.booklog.service;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import com.api.booklog.domain.UserEntity;
import com.api.booklog.repository.UsersRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UsersRepository userRepository;

    public CustomUserDetailsService(UsersRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 이메일로 사용자를 찾는다
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        return new User(
                user.getEmail(),               // username (이메일)
                user.getPassword(),            // password
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")) // 권한 (예시로 ROLE_USER)
        );
    }
}
