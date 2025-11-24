package com.basic.api.user.domain.model.dto.request.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
public class KakaoUserResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    @Getter
    @ToString
    @AllArgsConstructor
    public static class KakaoAccount {
        private String email; // Kakao 이메일
        private Profile profile;

        @Getter
        @ToString
        @AllArgsConstructor
        public static class Profile {
            private String nickname; // Kakao 이름
            private String profile_image_url; // Kakao 프로필
        }
    }
}
