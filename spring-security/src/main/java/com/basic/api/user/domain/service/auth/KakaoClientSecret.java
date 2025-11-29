package com.basic.api.user.domain.service.auth;

import com.basic.api.user.domain.model.dto.request.kakao.AccessTokenDto;
import com.basic.api.user.domain.model.dto.request.kakao.KakaoUserDto;

public interface KakaoClientSecret {
    // 카카오 서버로 부터 토큰 받기
    AccessTokenDto getKakaoAccessToken(String code);
    // 카카오 사용자 정보 받기
    KakaoUserDto getUserInfoFromKakao(String accessToken);
}
