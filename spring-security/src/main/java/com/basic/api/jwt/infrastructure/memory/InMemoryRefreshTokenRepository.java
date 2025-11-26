package com.basic.api.jwt.infrastructure.memory;

import com.basic.api.jwt.domain.model.entity.RefreshToken;
import com.basic.api.jwt.domain.repository.RefreshTokenRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryRefreshTokenRepository implements RefreshTokenRepository {
    private final Map<Long, RefreshToken> data = new ConcurrentHashMap<>();

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        data.put(Objects.requireNonNull(refreshToken).getUserId(), refreshToken);
        return refreshToken;
    }

    @Override
    public Optional<RefreshToken> findByUserId(Long userId) {
        return Optional.ofNullable(data.get(userId));
    }
}
