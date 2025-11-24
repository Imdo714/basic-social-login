package com.basic.api.jwt;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

public interface JwtProvider {
    String createToken(Long userId, String userName);

    String extractBearerToken(HttpServletRequest request);

    boolean validateToken(String token);

    Authentication getAuthentication(String token);
}
