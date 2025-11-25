package com.basic.api.user.application.socialLogin.apple;

import com.basic.api.user.domain.model.dto.response.LoginResponse;
import org.springframework.stereotype.Service;

@Service
public class AppleSocialServiceImpl implements AppleSocialService {

    @Override
    public LoginResponse appleSocialLogin(String identityToken) {
        return null;
    }
}
