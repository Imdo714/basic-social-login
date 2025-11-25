package com.basic.api.user.application.socialLogin.apple;

import com.basic.api.user.domain.model.dto.response.LoginResponse;

public interface AppleSocialService {
    LoginResponse appleSocialLogin(String identityToken);
}
