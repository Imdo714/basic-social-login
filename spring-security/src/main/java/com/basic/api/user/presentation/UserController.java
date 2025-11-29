package com.basic.api.user.presentation;

import com.basic.api.ApiResponse;
import com.basic.api.jwt.application.JwtTokenUseCase;
import com.basic.api.user.application.UserUseCase;
import com.basic.api.user.application.socialLogin.apple.AppleSocialService;
import com.basic.api.user.application.socialLogin.kakao.KakaoSocialLoginService;
import com.basic.api.user.domain.model.custom.CustomUserDetails;
import com.basic.api.user.domain.model.dto.request.AuthCodeDto;
import com.basic.api.user.domain.model.dto.response.LoginResponse;
import com.basic.api.user.domain.model.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
public class UserController {

    private final KakaoSocialLoginService kakaoSocialLoginService;
    private final AppleSocialService appleSocialService;
    private final UserUseCase userUseCase;
    private final JwtTokenUseCase jwtTokenUseCase;

    @GetMapping("/index")
    public ApiResponse<String> index() {
        return ApiResponse.ok("Hello World");
    }

    @GetMapping("/index2")
    public ApiResponse<String> index2(@AuthenticationPrincipal CustomUserDetails user) {
        log.info("getUserId = {}", user.getUserId());
        log.info("getUsername = {}", user.getUsername());
        return ApiResponse.ok("Hello World2");
    }

    @PostMapping("/kakao/login")
    public ApiResponse<LoginResponse> aaaLogin(@RequestBody AuthCodeDto payload) {
        return ApiResponse.ok(kakaoSocialLoginService.kakaoSocialLogin(payload.getCode()));
    }

    @PostMapping("/apple/login")
    public ApiResponse<LoginResponse> appleLogin(@RequestBody AuthCodeDto payload) {
        return ApiResponse.ok(appleSocialService.appleSocialLogin(payload.getCode()));
    }

    @PostMapping("/withdraw")
    public void withdraw(@AuthenticationPrincipal CustomUserDetails user) {
        appleSocialService.withdraw(user.getUserId());
    }

    @PostMapping("/logout2")
    public ApiResponse<String> logout(@AuthenticationPrincipal CustomUserDetails user,
            HttpServletRequest request
    ) {
        return ApiResponse.ok(jwtTokenUseCase.logout(user.getUserId(), request));
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<User> index(@PathVariable Long userId) {

        return ApiResponse.ok(userUseCase.getUserInfo(userId));
    }

}
