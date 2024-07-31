package com.api.booklog.controller;

import com.api.booklog.config.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
@Slf4j
@RestController
public class MainController {
    @GetMapping("/")
    public String main() {
        return "메인 페이지입니다.";
    }

    @GetMapping("/user")
    public String user(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long id = userPrincipal.getUserId();
        log.info(">>>>>>> 현재 로그인 된 user ID : {}", id);
        return "사용자 페이지입니다.😊😊";
    }
    @GetMapping("/admin")
    public String admin() {
        return "관리자 페이지입니다.😎😎";
    }

}
