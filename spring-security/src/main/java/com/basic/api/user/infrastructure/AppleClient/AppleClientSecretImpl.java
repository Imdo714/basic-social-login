package com.basic.api.user.infrastructure.AppleClient;

import com.basic.api.user.domain.model.dto.request.apple.AppleTokenResponse;
import com.basic.api.user.domain.service.AppleClientSecret;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Slf4j
@Service
public class AppleClientSecretImpl implements AppleClientSecret {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${APPLE_TEAM_ID}")
    private String teamId;

    @Value("${APPLE_KEY_ID}")
    private String keyId;

    @Value("${APPLE_CLIENT_ID}")
    private String clientId;

    @Value("${APPLE_PRIVATE_KEY}")
    private String privateKeyP8;

    @Override // Client Secret 생성
    public String createClientSecret() {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + 3600000); // 1시간 유효

        return Jwts.builder()
                .setHeaderParam(JwsHeader.KEY_ID, keyId) // kid
                .setIssuer(teamId)                       // iss
                .setAudience("https://appleid.apple.com") // aud
                .setSubject(clientId)                    // sub
                .setIssuedAt(now)                        // iat
                .setExpiration(expiration)               // exp
                .signWith(getPrivateKey(), SignatureAlgorithm.ES256) // 서명
                .compact();
    }

    @Override // 애플 서버로 토큰 요청
    public AppleTokenResponse requestAppleToken(String code, String clientSecret) {
        String appleUrl = "https://appleid.apple.com/auth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("code", code);
        params.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<AppleTokenResponse> response = restTemplate.postForEntity(appleUrl, request, AppleTokenResponse.class);
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("애플 토큰 요청 실패: " + e.getMessage());
        }
    }

    @Override // idToken 파싱
    public Map<String, Object> getAppleUserIdFromIdToken(String idToken) {
        try {
            String[] chunks = idToken.split("\\.");
            Base64.Decoder decoder = Base64.getUrlDecoder();
            String payload = new String(decoder.decode(chunks[1]));

            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(payload, Map.class);

        } catch (Exception e) {
            throw new RuntimeException("id_token 파싱 실패");
        }
    }

    @Override
    public void sendRevokeRequest(String clientSecret, String refreshToken) {
        String revokeUrl = "https://appleid.apple.com/auth/revoke";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", clientId); // 앱의 Bundle ID (Properties에서 가져온 값)
        params.add("client_secret", clientSecret); // 생성한 JWT
        params.add("token", refreshToken); // ★ DB에 저장했던 Refresh Token
        params.add("token_type_hint", "refresh_token"); // "이거 리프레시 토큰이야"라고 알려줌

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            // 응답 바디는 크게 중요하지 않아서 String으로 받거나 Void로 처리
            restTemplate.postForEntity(revokeUrl, request, String.class);
            log.info("애플 연결 해제(Revoke) 성공");
        } catch (Exception e) {
            // 여기서 예외가 터져도 상위 메서드(withdraw)에서 로그만 찍고 넘어갈 수 있게
            // RuntimeException으로 던지거나, 로그만 남기고 조용히 처리합니다.
            log.error("애플 연결 해제 요청 실패: {}", e.getMessage());
            throw new RuntimeException("애플 Revoke 실패");
        }
    }

    // PrivateKey 객체 생성 헬퍼 (BouncyCastle 라이브러리 필요할 수 있음)
    private PrivateKey getPrivateKey() {
        try {
            // p8 파일의 "-----BEGIN PRIVATE KEY-----" 등을 제거하고 내용만 가져와야 함
            String privateKeyContent = privateKeyP8
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");

            byte[] encoded = Base64.getDecoder().decode(privateKeyContent);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
            KeyFactory kf = KeyFactory.getInstance("EC");
            return kf.generatePrivate(keySpec);
        } catch (Exception e) {
            throw new RuntimeException("Private Key 생성 실패", e);
        }
    }
}
