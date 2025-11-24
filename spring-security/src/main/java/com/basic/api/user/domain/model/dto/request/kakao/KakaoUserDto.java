package com.basic.api.user.domain.model.dto.request.kakao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
@AllArgsConstructor
public class KakaoUserDto {
    private String email;
    private String name;
    private String profileImageUrl;


    public static KakaoUserDto of(KakaoUserResponse.KakaoAccount account, KakaoUserResponse.KakaoAccount.Profile profile) {
        return KakaoUserDto.builder()
                .email(account.getEmail())
                .name(profile.getNickname())
                .profileImageUrl(profile.getProfile_image_url())
                .build();
    }
}
