package com.api.booklog.service;

import com.api.booklog.domain.UserEditor;
import com.api.booklog.domain.Users;
import com.api.booklog.exception.AlreadyExistUserInformation;
import com.api.booklog.exception.Unauthorized;
import com.api.booklog.exception.UserNotFound;
import com.api.booklog.repository.UsersRepository;
import com.api.booklog.request.UserEdit;
import com.api.booklog.response.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UsersRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BookMarkService bookMarkService;

    public UserResponse getUserProfile(Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(UserNotFound::new);
        return new UserResponse(user);
    }

    @Transactional
    public void edit(Long id, UserEdit userEdit) {
        Users user = userRepository.findById(id)
                .orElseThrow(Unauthorized::new);
        log.info("현재 정보 출력 : {}", user.getName());

        // 이름 또는 이메일 중 하나라도 중복된 경우를 확인
        userRepository.findByNameOrEmail(userEdit.getName(), userEdit.getEmail())
                .ifPresent(existingUser -> {
                    // 현재 수정 중인 사용자와 다른 사용자의 이름 또는 이메일이면 예외 처리
                    if (!existingUser.getId().equals(id)) {
                        log.info("Existing User ID: {}, Current ID: {}", existingUser.getId(), user.getId());
                        log.info("[400 에러] 이미 존재하는 사용자 정보입니다.");
                        throw new AlreadyExistUserInformation();
                    }
                });

        // 사용자 정보 수정
        UserEditor.UserEditorBuilder userEditorBuilder = user.toEditor();

        // 비밀번호 암호화
        String encryptedPassword = passwordEncoder.encode(userEdit.getPassword());

        // 사용자 정보 업데이트
        UserEditor userEditor = userEditorBuilder
                .name(userEdit.getName())
                .email(userEdit.getEmail())
                .password(encryptedPassword)
                .build();
        user.edit(userEditor);

        log.info("사용자 정보가 성공적으로 업데이트되었습니다: ID = {}", id);
    }
    public void delete(Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow((Unauthorized::new));
        boolean isBookmarked = bookMarkService.isExistsKey(bookMarkService.makeKey(userId));
        if(isBookmarked) {
            bookMarkService.removeBookmarkByKey(user.getId());
        }
        userRepository.delete(user);
    }
}
