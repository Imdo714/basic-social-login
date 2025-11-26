package com.basic.api.jwt.domain.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReissueTokenResponse {
    private String accessToken;
}
