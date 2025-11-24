package com.basic.api.jwt;

import com.basic.api.user.domain.model.custom.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class JwtProviderImpl implements JwtProvider {

    @Value("${JWT_SECRET}")
    private String secretKey;

    @Value("${JWT_VALIDITY_SECONDS}")
    private long validityInMilliseconds;

    @Override
    public String createToken(Long userId, String userName) { // AccessToken 생성 메서드
        Claims claims = Jwts.claims().setSubject(userName);
        claims.put("userId", userId);

        Date now = new Date();
        Date expiry = new Date(now.getTime() + validityInMilliseconds);

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
    public boolean validateToken(String token) { // 토큰 서명값 검증
        try {
            Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
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

}
