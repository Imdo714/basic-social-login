package com.basic.api.user.presentation;

import com.basic.api.ApiResponse;
import com.basic.api.user.application.UserUseCase;
import com.basic.api.user.application.socialLogin.kakao.KakaoSocialLoginService;
import com.basic.api.user.domain.model.custom.CustomUserDetails;
import com.basic.api.user.domain.model.dto.response.LoginResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserUseCase userUseCase;
    private final KakaoSocialLoginService kakaoSocialLoginService;

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
    public ApiResponse<LoginResponse> aaaLogin(@RequestBody Map<String, String> payload) {
        String code = payload.get("code");
        log.info("code = {}", code);

        return ApiResponse.ok(kakaoSocialLoginService.kakaoSocialLogin(code));
    }

//    @PostMapping("/auth/apple")
//    public ApiResponse<LoginResponse> appleLogin(@RequestBody Map<String,String> body) {
//        String identityToken = body.get("identityToken");
//        return ApiResponse.ok(appleService.loginByIdentityToken(identityToken));
//    }
}
