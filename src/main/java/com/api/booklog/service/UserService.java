package com.api.booklog.service;

import com.api.booklog.domain.UserEditor;
import com.api.booklog.domain.UserEntity;
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

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UsersRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BookMarkService bookMarkService;

    public UserResponse getUserProfile(String email) {
        UserEntity user = findUserByEmail(email);
        return new UserResponse(user);
    }

    @Transactional
    public void edit(String email, UserEdit userEdit) {
        UserEntity user = findUserByEmail(email);
        log.info("현재 정보 출력 : {}", user.getName());

        //validate : 이름 또는 이메일 중 하나라도 중복된 경우를 확인
        Integer count = userRepository.findByNameOrEmail(userEdit.getName(), userEdit.getEmail());
        if(count > 0){
            throw new AlreadyExistUserInformation();
        }
        updateUser(user, userEdit);
        log.info("사용자 정보가 성공적으로 업데이트되었습니다: ID = {}", user.getId());
    }

    private void updateUser(UserEntity user, UserEdit userEdit) {
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
    }

    public void delete(String email) {
        UserEntity user = findUserByEmail(email);
        boolean isBookmarked = bookMarkService.isExistsKey(bookMarkService.makeKey(user.getId()));
        if(isBookmarked) {
            bookMarkService.removeBookmarkByKey(user.getId());
        }
        userRepository.delete(user);
    }

    private UserEntity findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(UserNotFound::new);
    }
}
