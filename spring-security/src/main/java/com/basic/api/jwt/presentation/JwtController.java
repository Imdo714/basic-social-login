package com.basic.api.jwt.presentation;

import com.basic.api.ApiResponse;
import com.basic.api.jwt.application.JwtTokenUseCase;
import com.basic.api.jwt.domain.model.dto.request.TokenReissueDto;
import com.basic.api.jwt.domain.model.dto.response.ReissueTokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class JwtController {

    private final JwtTokenUseCase jwtTokenUseCase;

    @PostMapping("/refresh")
    public ApiResponse<ReissueTokenResponse> refresh(@RequestBody TokenReissueDto tokenReissueDto) {
        return ApiResponse.ok(jwtTokenUseCase.reissueAccessToken(tokenReissueDto.getRefreshToken()));
    }
}
