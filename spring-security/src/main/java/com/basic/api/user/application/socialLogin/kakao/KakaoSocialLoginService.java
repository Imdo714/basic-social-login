package com.basic.api.user.application.socialLogin.kakao;

import com.basic.api.user.domain.model.dto.response.LoginResponse;

public interface KakaoSocialLoginService {
    LoginResponse kakaoSocialLogin(String code);
}
