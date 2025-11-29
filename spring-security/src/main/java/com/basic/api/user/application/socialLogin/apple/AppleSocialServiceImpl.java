package com.basic.api.user.application.socialLogin.apple;

import com.basic.api.jwt.application.JwtTokenUseCase;
import com.basic.api.user.domain.model.dto.request.apple.AppleTokenResponse;
import com.basic.api.user.domain.model.dto.response.LoginResponse;
import com.basic.api.user.domain.model.entity.User;
import com.basic.api.user.domain.repository.UserRepository;
import com.basic.api.user.domain.service.auth.AppleClientSecret;
import com.basic.global.exception.handelException.jwtException.RefreshTokenNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppleSocialServiceImpl implements AppleSocialService {

    private final UserRepository userRepository;
    private final JwtTokenUseCase jwtTokenUseCase;
    private final AppleClientSecret appleClientSecret;

    @Override
    public LoginResponse appleSocialLogin(String authorizationCode) {
        // Client Secret 생성
        String clientSecret = appleClientSecret.createClientSecret();

        // 애플 서버에 토큰 요청
        AppleTokenResponse appleTokenResponse = appleClientSecret.requestAppleToken(authorizationCode, clientSecret);
        log.info("Apple Token Response: {}", appleTokenResponse);

        // id_token에서 사용자 정보 추출
        Map<String, Object> claims = appleClientSecret.getAppleUserIdFromIdToken(appleTokenResponse.getIdToken());
        String appleUserId = (String) claims.get("sub");  // 고유 ID
        String email = (String) claims.get("email");      // 이메일

        log.info("Apple User ID: {}", appleUserId);
        log.info("Apple User Email: {}", email);

        User user = registerOrLogin(appleUserId, appleTokenResponse, email);
        String refreshToken = jwtTokenUseCase.createAndSaveRefreshToken(user.getId(), user.getName());
        String accessToken = jwtTokenUseCase.createAccessToken(user.getId(), user.getName());

        return LoginResponse.of(user, accessToken, refreshToken);
    }

    @Override
    public void withdraw(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RefreshTokenNotFoundException("존재하지 않는 유저입니다."));

        // 애플 로그인 유저라면 socialRefreshToken이 반드시 있어야 함
        if (user.getSocialRefreshToken() != null) {
            try {
                String clientSecret = appleClientSecret.createClientSecret();

                // 애플 서버에 계정 탈퇴 요청
                appleClientSecret.sendRevokeRequest(clientSecret, user.getSocialRefreshToken());
            } catch (Exception e) {
                // 중요: 애플 서버 통신 실패하더라도 우리 DB에서는 지워야 할까?
                // 보통은 로그만 남기고 회원 삭제는 진행하는 것이 UX상 좋다.
                log.error("애플 연결 실패 계절 탈퇴 실패 : {}", e.getMessage());
            }
        }
    }

    private User registerOrLogin(String appleUserId, AppleTokenResponse appleTokenResponse, String email) {
        return userRepository.findByProviderId(appleUserId)
                .map(user -> {
                    log.info("Apple Login !!!");
                    if (appleTokenResponse.getRefreshToken() != null) {
                        user.updateSocialRefreshToken(appleTokenResponse.getRefreshToken());
                    }
                    return user;
                })
                .orElseGet(() -> {
                    log.info("Apple register !!!");
                    User newUser = User.createAppleUserBuilder(appleUserId, email, appleTokenResponse.getRefreshToken());
                    return userRepository.save(newUser);
                });
    }

}
