package com.basic.api.jwt.domain.repository;

import com.basic.api.jwt.domain.model.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository {
    RefreshToken save(RefreshToken refreshToken);
    Optional<RefreshToken> findByUserId(Long userId);
}
