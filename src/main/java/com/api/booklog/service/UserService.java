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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
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

    // 사용자 정보 변경
    @Transactional
    public void edit(Long id, UserEdit userEdit) {
        Users user = userRepository.findById(id)
                .orElseThrow((Unauthorized::new));

        // 중복된 이름 또는 이메일이 있는지 확인 (본인의 정보 제외)
        userRepository.findByNameOrEmail(userEdit.getName(), userEdit.getEmail())
                .ifPresent(existingUser -> {
                    // 현재 수정 중인 사용자와 다른 사용자의 이름 또는 이메일이면 예외 처리
                    if (!existingUser.getId().equals(id)) {
                        throw new AlreadyExistUserInformation();
                    }
                });
        UserEditor.UserEditorBuilder userEditorBuilder = user.toEditor();

        // 변경된 비밀번호 암호화
        String encryptedPassword = passwordEncoder.encode(userEdit.getPassword());
        UserEditor userEditor = userEditorBuilder
                // 수정이 없어 null 값이 들어온다면,
                // 기존의 db에 저장된 사용자의 필드값이 저장됨.
                .name(userEdit.getName())
                .email(userEdit.getEmail())
                .password(encryptedPassword)
                .build();
        user.edit(userEditor);
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
