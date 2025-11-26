package com.basic.api.jwt.domain.service;

import com.basic.api.user.domain.model.custom.CustomUserDetails;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class JwtProviderImpl implements JwtProvider {

    @Value("${JWT_SECRET}")
    private String secretKey;

    @Value("${JWT_ACCESS_EXPIRATION}")
    private long accessTokenValidity;

    @Value("${JWT_REFRESH_EXPIRATION}")
    private long refreshTokenValidity;

    @Override
    public String createAccessToken(Long userId, String userName) {
        Claims claims = Jwts.claims().setSubject(userName);
        claims.put("userId", userId);
        return createToken(claims, accessTokenValidity);
    }

    @Override
    public String createRefreshToken(Long userId, String userName) {
        Claims claims = Jwts.claims().setSubject(userName);
        claims.put("userId", userId);
        return createToken(claims, refreshTokenValidity);
    }

    private String createToken(Claims claims, long validity) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + validity);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    @Override
    public String extractBearerToken(HttpServletRequest request) { // Authorization 헤더에서 Bearer 토큰을 추출
        String bearer = request.getHeader("Authorization");
        return (bearer != null && bearer.startsWith("Bearer ")) ? bearer.substring(7) : null;
    }

    @Override
    public void validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            throw e;
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtException("유효하지 않은 토큰입니다.");
        }
    }

    @Override
    public Authentication getAuthentication(String token) { // 토큰에서 User 정보를 꺼내서 Authentication 타입으로 반환
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();

        String username = claims.getSubject();
        Long userId = Long.valueOf(claims.get("userId").toString());

        CustomUserDetails principal = new CustomUserDetails(userId, username);

        return new UsernamePasswordAuthenticationToken(principal, "", List.of());
    }

    @Override
    public Long getExpiration(String token) {
        // 토큰의 만료 시간 - 현재 시간 = 남은 시간
        Date expiration = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expiration.getTime() - new Date().getTime();
    }

}
