package com.basic.api.user.application.socialLogin.kakao;

import com.basic.api.jwt.JwtProvider;
import com.basic.api.user.domain.model.dto.request.kakao.AccessTokenDto;
import com.basic.api.user.domain.model.dto.request.kakao.KakaoUserDto;
import com.basic.api.user.domain.model.dto.request.kakao.KakaoUserResponse;
import com.basic.api.user.domain.model.dto.response.LoginResponse;
import com.basic.api.user.domain.model.entity.User;
import com.basic.api.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoSocialLoginServiceImpl implements KakaoSocialLoginService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    @Value("${KAKAO_CLIENT_ID}")
    private String client_id;

    @Value("${KAKAO_REDIRECT_URL}")
    private String redirect_url;

    @Override
    public LoginResponse kakaoSocialLogin(String code) {
        AccessTokenDto kakaoAccessToken = getKakaoAccessToken(code);
        log.info("kakaoAccessToken = {}", kakaoAccessToken);

        KakaoUserDto userInfo = getUserInfoFromKakao(kakaoAccessToken.getAccessToken());
        log.info("userInfo = {}", userInfo);

        User user = registerOrLogin(userInfo);
        return LoginResponse.of(user, jwtProvider.createToken(user.getId(), user.getName()));
    }

    private AccessTokenDto getKakaoAccessToken(String code) {
        RestClient restClient = RestClient.create();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", client_id);
        params.add("redirect_uri", redirect_url);
        params.add("code", code);

        ResponseEntity<AccessTokenDto> response = restClient.post()
                .uri("https://kauth.kakao.com/oauth/token")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .body(params)
                .retrieve()
                .toEntity(AccessTokenDto.class);

        return response.getBody();
    }

    private KakaoUserDto getUserInfoFromKakao(String accessToken) {
        RestClient restClient = RestClient.create();

        KakaoUserResponse response = restClient.get()
                .uri("https://kapi.kakao.com/v2/user/me")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .body(KakaoUserResponse.class);

        KakaoUserResponse.KakaoAccount account = response.getKakaoAccount();
        log.info("account = {}", account);

        KakaoUserResponse.KakaoAccount.Profile profile = account.getProfile();
        log.info("profile = {}", profile);

        return KakaoUserDto.of(response.getKakaoAccount(), response.getKakaoAccount().getProfile());
    }

    private User registerOrLogin(KakaoUserDto kakaoUser) {
        return userRepository.findByEmail(kakaoUser.getEmail())
                .orElseGet(() -> {
                    User newUser = User.createKakaoUserBuilder(kakaoUser);
                    return userRepository.save(newUser);
                });
    }

}
