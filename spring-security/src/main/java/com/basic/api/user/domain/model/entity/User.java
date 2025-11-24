package com.basic.api.user.domain.model.entity;

import com.basic.api.user.domain.model.dto.request.kakao.KakaoUserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class User {
    private Long id;
    private String email;
    private String name;
    private String profileImageUrl;

    public static User createKakaoUserBuilder(KakaoUserDto kakaoUser) {
        return User.builder()
                .email(kakaoUser.getEmail())
                .name(kakaoUser.getName())
                .profileImageUrl(kakaoUser.getProfileImageUrl())
                .build();
    }

    public void setId(Long id) {
        this.id = id;
    }
}
