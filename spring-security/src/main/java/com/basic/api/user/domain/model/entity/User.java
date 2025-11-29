package com.basic.api.user.domain.model.entity;

import com.basic.api.user.domain.model.dto.request.kakao.KakaoUserDto;
import com.basic.api.user.domain.model.type.Provider;
import com.basic.api.user.domain.model.type.UserStatus;
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

    // 탈퇴 기능을 위해서 추가 가능성
    private Provider provider; // 가입 경로
    private String providerId; // 소셜에서 주는 식별 ID
    private String socialRefreshToken; // 애플은 처음에 RefreshToken을 주는데 무조건 저장해야 함 탈퇴를 하기 위해서
    private UserStatus status; // 회원 상태

    public static User createKakaoUserBuilder(KakaoUserDto kakaoUser, String socialRefreshToken) {
        return User.builder()
                .email(kakaoUser.getEmail())
                .name(kakaoUser.getName())
                .profileImageUrl(kakaoUser.getProfileImageUrl())
                .provider(Provider.KAKAO)
                .socialRefreshToken(socialRefreshToken)
                .status(UserStatus.ACTIVE)
                .build();
    }

    public static User createAppleUserBuilder(String appleUserId, String email, String appleRefreshToken) {
        return User.builder()
                .email(email)
                .provider(Provider.APPLE)
                .providerId(appleUserId)
                .socialRefreshToken(appleRefreshToken)
                .status(UserStatus.ACTIVE)
                .build();
    }

    public void updateSocialRefreshToken(String newToken) {
        if (newToken != null && !newToken.isBlank()) {
            this.socialRefreshToken = newToken;
        }
    }

    public void setId(Long id) {
        this.id = id;
    }
}
