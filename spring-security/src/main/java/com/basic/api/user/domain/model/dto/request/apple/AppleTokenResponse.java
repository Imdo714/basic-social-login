package com.basic.api.user.domain.model.dto.request.apple;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class AppleTokenResponse {
    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("expires_in")
    private int expiresIn;

    @JsonProperty("id_token")
    private String idToken; // 여기에 유저 정보가 들어있음

    @JsonProperty("refresh_token")
    private String refreshToken; // 꼭 DB에 저장해야 함 (나중에 탈퇴시 필요)

    @JsonProperty("token_type")
    private String tokenType;
}