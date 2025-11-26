package com.basic.api.jwt.application;

import com.basic.api.jwt.domain.model.dto.response.ReissueTokenResponse;
import com.basic.api.jwt.domain.model.entity.RefreshToken;
import com.basic.api.jwt.domain.repository.RefreshTokenRepository;
import com.basic.api.jwt.domain.service.JwtProvider;
import com.basic.api.user.domain.model.custom.CustomUserDetails;
import com.basic.global.exception.handelException.jwtException.JwtExpiredException;
import com.basic.global.exception.handelException.jwtException.JwtInvalidException;
import com.basic.global.exception.handelException.jwtException.RefreshTokenNotFoundException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtTokenUseCaseImpl implements JwtTokenUseCase {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;

    @Override
    public String createAndSaveRefreshToken(Long userId, String userName) {
        String refreshToken = jwtProvider.createRefreshToken(userId, userName);

        refreshTokenRepository.findByUserId(userId)
                .ifPresentOrElse(
                        existingToken -> existingToken.updateRefreshToken(refreshToken), // 기존 토큰이 있으면, 업데이트
                        () -> refreshTokenRepository.save(new RefreshToken(userId, refreshToken)) // 토큰이 없으면, 새로 저장
                );
        return refreshToken;
    }

    @Override
    public String createAccessToken(Long userId, String userName) {
        return jwtProvider.createAccessToken(userId,  userName);
    }

    @Override
    public ReissueTokenResponse reissueAccessToken(String refreshToken) {
        validateRefreshToken(refreshToken);

        // 토큰에서 유저 정보 추출
        Authentication authentication = jwtProvider.getAuthentication(refreshToken);
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        // DB에서 해당 유저의 저장된 Refresh Token 가져오기
        RefreshToken storedToken = refreshTokenRepository.findByUserId(userDetails.getUserId())
                .orElseThrow(() -> new RefreshTokenNotFoundException("로그아웃 된 사용자입니다. (DB에 토큰 없음)"));

        // 클라이언트 토큰, DB 토큰 일치 여부 확인
        if (!storedToken.getRefreshToken().equals(refreshToken)) {
            throw new JwtInvalidException("토큰 정보가 일치하지 않습니다.");
        }

        return new ReissueTokenResponse(jwtProvider.createAccessToken(userDetails.getUserId(), userDetails.getUsername()));
    }

    private void validateRefreshToken(String token) {
        try {
            jwtProvider.validateToken(token);
        } catch (ExpiredJwtException e) {
            throw new JwtExpiredException("Refresh Token이 만료되었습니다. 다시 로그인해주세요.");
        } catch (JwtException e) {
            throw new JwtInvalidException("유효하지 않은 Refresh Token입니다.");
        }
    }

}
