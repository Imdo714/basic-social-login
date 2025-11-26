package com.basic.api.jwt.application;

import com.basic.api.jwt.domain.model.dto.response.ReissueTokenResponse;

public interface JwtTokenUseCase {
    // RefreshToken 생성 및 DB 저장
    String createAndSaveRefreshToken(Long userId, String userName);
    // AccessToken 생성
    String createAccessToken(Long userId, String userName);
    // RefreshToken으로 AccessToken 생성
    ReissueTokenResponse reissueAccessToken(String refreshToken);
}
