package com.basic.api.user.application.socialLogin.kakao;

import com.basic.api.jwt.application.JwtTokenUseCase;
import com.basic.api.user.domain.model.dto.request.kakao.AccessTokenDto;
import com.basic.api.user.domain.model.dto.request.kakao.KakaoUserDto;
import com.basic.api.user.domain.model.dto.response.LoginResponse;
import com.basic.api.user.domain.model.entity.User;
import com.basic.api.user.domain.repository.UserRepository;
import com.basic.api.user.domain.service.auth.KakaoClientSecret;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoSocialLoginServiceImpl implements KakaoSocialLoginService {

    private final UserRepository userRepository;
    private final JwtTokenUseCase jwtTokenUseCase;
    private final KakaoClientSecret kakaoClientSecret;

    @Override
    public LoginResponse kakaoSocialLogin(String code) {
        AccessTokenDto kakaoAccessToken = kakaoClientSecret.getKakaoAccessToken(code);
        log.info("kakaoAccessToken = {}", kakaoAccessToken);

        KakaoUserDto userInfo = kakaoClientSecret.getUserInfoFromKakao(kakaoAccessToken.getAccessToken());
        log.info("userInfo = {}", userInfo);

        User user = registerOrLogin(userInfo, kakaoAccessToken.getRefreshToken());
        String refreshToken = jwtTokenUseCase.createAndSaveRefreshToken(user.getId(), user.getName());
        String accessToken = jwtTokenUseCase.createAccessToken(user.getId(), user.getName());

        return LoginResponse.of(user, accessToken, refreshToken);
    }

    private User registerOrLogin(KakaoUserDto kakaoUser, String socialRefreshToken) {
        return userRepository.findByEmail(kakaoUser.getEmail())
                .map(user -> {
                    // user가 있으면 RefreshToken 업데이트
                    user.updateSocialRefreshToken(socialRefreshToken);
                    return user;
                })
                .orElseGet(() -> {
                    // user가 없다면 DB에 저장
                    User newUser = User.createKakaoUserBuilder(kakaoUser, socialRefreshToken);
                    return userRepository.save(newUser);
                });
    }

}
