package com.basic.api.user.domain.service.auth;

import com.basic.api.user.domain.model.dto.request.apple.AppleTokenResponse;

import java.util.Map;

public interface AppleClientSecret {
    // Client Secret 생성
    String createClientSecret();
    // 애플 서버로 토큰 요청
    AppleTokenResponse requestAppleToken(String authorizationCode, String clientSecret);
    // idToken 파싱
    Map<String, Object> getAppleUserIdFromIdToken(String idToken);
    // 애플 서버에 계정 탈퇴 요청
    void sendRevokeRequest(String clientSecret, String socialRefreshToken);
}
